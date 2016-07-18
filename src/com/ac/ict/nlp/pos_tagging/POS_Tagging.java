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

	private String[] text; // 存储语料库中的词组和词性
	private String[] phrase; // 存储语料库中的词组
	private String[] characters; // 存储语料库中的词性
	Hashtable<String, Integer> charactersHash = new Hashtable<String, Integer>(); // 存储词性和频率
	Hashtable<String, Integer> phraseHash = new Hashtable<String, Integer>(); // 存储词组和频率
	Hashtable<String, Integer> transformFrequencyHash = new Hashtable<String, Integer>(); // 存储两个连续词组和频数
	Hashtable<String, Integer> emissonFrequencyHash = new Hashtable<String, Integer>(); // 存储词组极其词性的频数
	private int charactersNum = 0;// 词性的个数
	private int phraseNum = 0;// 词组的个数
	private String[] diffChars;// 只存储词性
	private String[] diffPhras;// 只存储词组
	Hashtable<String, Integer> phrasePosition = new Hashtable<String, Integer>();// 保存词组在diffPhras中的位置
	private double[] prioriProbability;// 每个词性数在总词性数的比例
	private double[][] transformProbability;// 从一个词性转移到另一个词性的概率
	private double[][] emissionProbability;// 在某个词性下是某个词组的概率

	/*
	 * 载入词性标注语料库，统计出相关属性
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
		// 获取语料库中的词组和相应的词性
		text = content.toString().split("\\s{1,}");
		// 获取语料库中的词组
		phrase = content.toString().split("(/[a-z]*\\s{0,})");
		// 获取语料库中的词性
		characters = content.toString().split("[0-9|-]*/|\\s{1,}[^a-z]*");
	}

	/*
	 * 统计不同的词性及其频率 charactersHash：保存不同的词性及其频率 diffChars：只保存不同的词性
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
	 * 统计语料库中的不同词组 phraseHash：用来保存词组和频率 diffPhras：只用来保存不同的词组
	 * phrasePosition：保存词组在diffPhras中的位置
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
	 * 计算状态转移频数,即语料库中两个连续词性出现的频数
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
	 * 计算词组极其词性的频数
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
	 * 计算词性的先验概率,即每个词性数在总词性数的比例
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
	 * 计算状态转移概率,即从一个词性转移到另一个词性的概率
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
	 * 计算观察概率,即在某个词性下是某个词组的概率
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
	 * 由于采用的分词方法是从左往右，最大匹配模式。但是程序中采用的语料库却倾向于
	 * 最小匹配模式。所以初次分词的结果有可能不在语料库中。故将语料库不能识别的词组再次进行分词尝试让算法找到更多的词。
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
	 * 使用viterbi算法对给定的字符串数组进行词性标注
	 */
	public void viterbi(String[] example) {
		double[][] value = new double[example.length][charactersNum];// 某个词组i选择词性j的最大可能
		int[][] previous = new int[example.length][charactersNum];// value取得最大值时的前驱状态
		int position;
		//// 初始化第一个字在不同词性下的value，即参数PI
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
		// 一步步计算value,并保存previous
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
		// 向前回溯最大取值时的前一个状态
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
