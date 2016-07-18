/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ac.ict.nlp.pos_tagging;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author lvshanshan,zhaoruizhuo
 */
public class POS_Tagging {

	private String[] text; // �洢���Ͽ��еĴ���ʹ���
	private String[] phrase; // �洢���Ͽ��еĴ���
	private String[] characters; // �洢���Ͽ��еĴ���
	Hashtable<String, Integer> charactersHash = new Hashtable<String, Integer>(); // �洢���Ժ�Ƶ��
	Hashtable<String, Integer> phraseHash = new Hashtable<String, Integer>(); // �洢�����Ƶ��
	Hashtable<String, Integer> transformFrequencyHash = new Hashtable<String, Integer>(); // �洢�������������Ƶ��
	Hashtable<String, Integer> emissonFrequencyHash = new Hashtable<String, Integer>(); // �洢���鼫����Ե�Ƶ��
	private int charactersNum = 0;// ���Եĸ���
	private int phraseNum = 0;// ����ĸ���
	private String[] diffChars;// ֻ�洢����
	private String[] diffPhras;// ֻ�洢����
	Hashtable<String, Integer> phrasePosition = new Hashtable<String, Integer>();// ���������diffPhras�е�λ��
	private double[] prioriProbability;// ÿ�����������ܴ������ı���
	private double[][] transformProbability;// ��һ������ת�Ƶ���һ�����Եĸ���
	private double[][] emissionProbability;// ��ĳ����������ĳ������ĸ���

	/*
	 * ������Ա�ע���Ͽ⣬ͳ�Ƴ��������
	 */
	public void readCorpus(String fileName) {
		StringBuffer content = new StringBuffer();
		BufferedReader reader = null;
		try {
			String filePath = System.getProperty("user.dir") + fileName;
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("close file error");
				}
			}
		}
		// ��ȡ���Ͽ��еĴ������Ӧ�Ĵ���
		text = content.toString().split("\\s{1,}");
		// ��ȡ���Ͽ��еĴ���
		phrase = content.toString().split("(/[a-z]*\\s{0,})");
		// ��ȡ���Ͽ��еĴ���
		characters = content.toString().split("[0-9|-]*/|\\s{1,}[^a-z]*");
	}

	/*
	 * ͳ�Ʋ�ͬ�Ĵ��Լ���Ƶ�� charactersHash�����治ͬ�Ĵ��Լ���Ƶ�� diffChars��ֻ���治ͬ�Ĵ���
	 */
	public void characterSum() {
		for (int i = 1; i < characters.length; i++) {
			if (charactersHash.containsKey(characters[i])) {
				charactersHash.put(characters[i], charactersHash.get(characters[i]) + 1);
			} else {
				charactersHash.put(characters[i], 1);
			}
		}
		charactersNum = charactersHash.size();
		diffChars = new String[charactersNum];
		Enumeration<String> key = charactersHash.keys();
		for (int i = 0; i < charactersHash.size(); i++) {
			diffChars[i] = (String) key.nextElement();
		}
	}

	/*
	 * ͳ�����Ͽ��еĲ�ͬ���� phraseHash��������������Ƶ�� diffPhras��ֻ�������治ͬ�Ĵ���
	 * phrasePosition�����������diffPhras�е�λ��
	 */
	public void phraseSum() {
		for (int i = 0; i < phrase.length; i++) {
			if (phraseHash.containsKey(phrase[i])) {
				phraseHash.put(phrase[i], phraseHash.get(phrase[i]) + 1);
			} else {
				phraseHash.put(phrase[i], 1);
			}
		}
		phraseNum = phraseHash.size();
		diffPhras = new String[phraseNum];
		Enumeration<String> key = phraseHash.keys();
		for (int i = 0; i < phraseHash.size(); i++) {
			String str = (String) key.nextElement();
			diffPhras[i] = str;
			phrasePosition.put(str, i);
		}
	}

	/*
	 * ����״̬ת��Ƶ��,�����Ͽ��������������Գ��ֵ�Ƶ��
	 */
	public void transformFrequencySum() {
		for (int i = 0; i < characters.length - 1; i++) {
			String temp = characters[i] + "," + characters[i + 1];
			if (transformFrequencyHash.containsKey(temp)) {
				transformFrequencyHash.put(temp, transformFrequencyHash.get(temp) + 1);
			} else {
				transformFrequencyHash.put(temp, 1);
			}
		}
	}

	/*
	 * ������鼫����Ե�Ƶ��
	 */
	public void emissonFrequencySum() {
		for (int i = 0; i < text.length; i++) {
			if (emissonFrequencyHash.containsKey(text[i])) {
				emissonFrequencyHash.put(text[i], emissonFrequencyHash.get(text[i]) + 1);
			} else {
				emissonFrequencyHash.put(text[i], 1);
			}
		}
	}

	/*
	 * ������Ե��������,��ÿ�����������ܴ������ı���
	 */
	public void calculatePrioriProbability() {
		prioriProbability = new double[charactersNum];
		int allCharacterCount = 0;
		for (int i = 0; i < charactersNum; i++) {
			allCharacterCount += charactersHash.get(diffChars[i]);
		}
		for (int i = 0; i < charactersNum; i++) {
			prioriProbability[i] = charactersHash.get(diffChars[i]) * 1.0 / allCharacterCount;
		}
	}

	/*
	 * ����״̬ת�Ƹ���,����һ������ת�Ƶ���һ�����Եĸ���
	 */
	public void calculateTransformProbability() {
		transformProbability = new double[charactersNum][charactersNum];
		for (int i = 0; i < charactersNum; i++) {
			for (int j = 0; j < charactersNum; j++) {
				String front = diffChars[i];
				String last = diffChars[j];

				if (transformFrequencyHash.containsKey(front + "," + last)) {
					int numerator = transformFrequencyHash.get(front + "," + last);
					int denominator = charactersHash.get(front);
					transformProbability[i][j] = numerator * 100.0 / denominator;
				}
			}
		}
	}

	/*
	 * ����۲����,����ĳ����������ĳ������ĸ���
	 */
	public void calculateEmissionProbability() {
		emissionProbability = new double[charactersNum][phraseNum];
		for (int i = 0; i < charactersNum; i++) {
			for (int j = 0; j < phraseNum; j++) {
				String chars = diffChars[i];
				String phras = diffPhras[j];
				if (emissonFrequencyHash.containsKey(phras + "/" + chars)) {
					int numerator = emissonFrequencyHash.get(phras + "/" + chars);
					int denominator = charactersHash.get(chars);
					emissionProbability[i][j] = numerator * 100.0 / denominator;
				}
			}
		}
	}

	/*
	 * ���ڲ��õķִʷ����Ǵ������ң����ƥ��ģʽ�����ǳ����в��õ����Ͽ�ȴ������
	 * ��Сƥ��ģʽ�����Գ��ηִʵĽ���п��ܲ������Ͽ��С��ʽ����Ͽⲻ��ʶ��Ĵ����ٴν��зִʳ������㷨�ҵ�����Ĵʡ�
	 */
	public ArrayList<String> smallSeg(ArrayList<String> seg) {
		ArrayList<String> smallArrayList = new ArrayList<String>();
		for (int i = 0; i < seg.size(); i++) {
			String temp = seg.get(i);
			boolean canSpilt = false;
			int index = 0;
			if (phrasePosition.get(temp) == null) {
				for (int j = 1; j < temp.length(); j++) {
					if (phrasePosition.get(temp.substring(0, j)) != null
							&& phrasePosition.get(temp.substring(j)) != null) {
						canSpilt = true;
						index = j;
						break;
					}
				}
			}
			if (canSpilt) {
				smallArrayList.add(temp.substring(0, index));
				smallArrayList.add(temp.substring(index));
			} else {
				smallArrayList.add(temp);
			}
		}
		return smallArrayList;
	}

	/*
	 * ʹ��viterbi�㷨�Ը������ַ���������д��Ա�ע
	 */
	public void viterbi(String[] example) {
		double[][] value = new double[example.length][charactersNum];// ĳ������iѡ�����j��������
		int[][] previous = new int[example.length][charactersNum];// valueȡ�����ֵʱ��ǰ��״̬
		int position;
		//// ��ʼ����һ�����ڲ�ͬ�����µ�value��������PI
		if (phrasePosition.get(example[0]) != null) {
			position = phrasePosition.get(example[0]);
			for (int j = 0; j < charactersNum; j++) {
				value[0][j] = prioriProbability[j] * emissionProbability[j][position];
			}
		} else {
			for (int j = 0; j < charactersNum; j++) {
				value[0][j] = 1;
			}
		}
		// һ��������value,������previous
		for (int i = 1; i < example.length; i++) {
			if (phrasePosition.get(example[i]) == null) {
				for (int j = 0; j < charactersNum; j++) {
					value[i][j] = 1;
				}
				continue;
			}
			position = phrasePosition.get(example[i]);
			for (int j = 0; j < charactersNum; j++) {
				double max = value[i - 1][0] * transformProbability[0][j] * emissionProbability[j][position];
				int index = 0;
				for (int k = 1; k < charactersNum; k++) {
					value[i][j] = value[i - 1][k] * transformProbability[k][j] * emissionProbability[j][position];
					if (value[i][j] > max) {
						index = k;
						max = value[i][j];
					}
				}

				previous[i][j] = index;
				value[i][j] = max;
			}
		}
		// ��ǰ�������ȡֵʱ��ǰһ��״̬
		double max = -1;
		int index = 0;
		for (int i = 0; i < charactersNum; i++) {
			if (max < value[example.length - 1][i]) {
				index = i;
				max = value[example.length - 1][i];
			}
		}
		for (int i = example.length - 1; i >= 0; i--) {
			example[i] = example[i].concat("/" + diffChars[index]);
			index = previous[i][index];
		}
	}

}
