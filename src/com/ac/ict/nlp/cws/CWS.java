/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ac.ict.nlp.cws;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author wanghongyang
 */
public class CWS {

    public static int BEGIN = 19968, LAST = 40896;  //���Ĵʵķ�Χ
    ArrayList<String> dictionary = new ArrayList<String>();  //�ʵ�
    Hashtable<String, Integer> groupStart = new Hashtable<String, Integer>(); //��һ������ĸ��ͬ�ʵĿ�ʼ�±�λ��
    Hashtable<String, Integer> groupLength = new Hashtable<String, Integer>(); //����ĸ��ͬ�ʵĸ���
    Hashtable<String, Integer> groupMaxLength = new Hashtable<String, Integer>(); //����ĸ��ͬ�ʵ����ʳ���

    /*
        ���شʵ䲢��Ԥ����
    */
    public void loadDic(String lib) {
        try {
            String filePath = System.getProperty("user.dir") + lib;    
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));   
			BufferedReader bfl = new BufferedReader(new InputStreamReader(in, "gb2312"));
            String lineString;
            String first = "һ";
            int count = 0;//ͳ������ĸ��ͬ�ʵĸ���
            int i = 0;
            int maxLength = 0;//����ĸ��ͬ���е����ʳ���
            groupStart.put(first, 0);
            while ((lineString = bfl.readLine()) != null) {
                String temp = lineString.substring(0, 1);
                if (!temp.equals(first)) {//����ͬ����ĸ��
                    groupLength.put(first, count);//�洢����ĸ��ͬ�ʵĸ���
                    groupMaxLength.put(first, maxLength);//�洢����ĸ��ͬ�ʵ����ʳ���
                    groupStart.put(temp, i);//�洢����ʵĿ�ʼ�±�
                    //������ر���
                    maxLength = 0;
                    count = 1;
                    first = temp;
                } else//����ͬ����ĸ��
                {
                    count++;
                }
                int index = lineString.indexOf(" ");
                if (index != -1) {
                    lineString = lineString.substring(0, index);
                }
                if (maxLength < lineString.length())
                {
                    maxLength = lineString.length();//����ͬ����е����ʳ���
                }
                dictionary.add(i++, lineString);//���ôʴ��ڴʵ���
            }
            groupLength.put(first, count);
            groupMaxLength.put(first, maxLength);
            bfl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * �ж��Ƿ�Ϊ���Ĵ�
    */
    public static boolean isCHWord(char s) {
        boolean res = false;
        int code = (int) s;
        if (code >= CWS.BEGIN && code <= CWS.LAST) {
            res = true;
        }
        return res;
    }

    /*
    * ���ַ����Ҹô��ڴʵ��е�λ��
    */
    public int BiSearch(String w) {
        int res = -1;
        if (w != null && w.length() > 0) {
            String head = w.substring(0, 1);
            int length = groupLength.get(head);
            int start = groupStart.get(head);
            int end = start + length - 1;
            if (start != -1 && end != -1) {
                if (start == end) {
                    if (dictionary.get(start).equals(w)) {
                        res = start;
                    }
                } else {
                    while (start <= end) {
                        int mid = (start + end) / 2;
                        int comp = w.compareTo(dictionary.get(mid));
                        if (comp == 0) {
                            return mid;
                        } else if (comp > 0) {
                            start = mid + 1;
                        } else {
                            end = mid - 1;
                        }
                    }

                    if (w.equals(dictionary.get(start))) {
                        return start;
                    }
                    if (w.equals(dictionary.get(end))) {
                        return end;
                    }
                }
            }
        }
        return res;
    }

    /*
     * �ִ�
    */
    public ArrayList<String> segmentation(String source) {
        ArrayList<String> seg = new ArrayList<String>();
        for (int i = 0; i < source.length(); i++) {
            String temp = source.substring(i, i + 1);
            int front = i;
            //������Ǻ���,�����д���
            if (!isCHWord(temp.charAt(0))) {
                //���i�Ĵ�С�Ƿ񳬹��ַ�������
                while (i < source.length() - 1 && !isCHWord(source.charAt(++i)));
                if (i == source.length() - 1) {
                    seg.add(source.substring(front));
                    break;
                }
                if (source.substring(front, i).trim().length() != 0) {
                    seg.add(source.substring(front, i--));
                } else {
                    i--;
                }
                continue;
            }
            int position = 0;
            boolean found = false;
            // ���ƥ��ԭ����Ҵ���
            int j;
            if (groupMaxLength.get(temp) != null) { //�ʵ����иô���
                for (j = groupMaxLength.get(temp); j >= 1; j--) {
                    String w;
                    if ((i + j) >= source.length()) {
                        w = source.substring(i).trim();
                    } else {
                        w = source.substring(i, i + j).trim();
                    }
                    int previous;
                    if ((previous = BiSearch(w)) < 0)//���û�иó��ȵģ��������Ҹ��̳��ȵ�
                    {
                        continue;
                    }
                    found = true;
                    position = previous;
                    break;
                }
            }
            if (found) {
                seg.add(dictionary.get(position));
                i += dictionary.get(position).length() - 1;
            } else //�Ҳ����ͽ���������Ϊһ���ָ�
            {
                seg.add(temp);
            }
        }
        return seg;
    }

}
