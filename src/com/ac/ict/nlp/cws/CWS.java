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

    public static int BEGIN = 19968, LAST = 40896;  //中文词的范围
    ArrayList<String> dictionary = new ArrayList<String>();  //词典
    Hashtable<String, Integer> groupStart = new Hashtable<String, Integer>(); //第一个首字母不同词的开始下表位置
    Hashtable<String, Integer> groupLength = new Hashtable<String, Integer>(); //首字母相同词的个数
    Hashtable<String, Integer> groupMaxLength = new Hashtable<String, Integer>(); //首字母相同词的最大词长度

    /*
        加载词典并做预处理
    */
    public void loadDic(String lib) {
        try {
            String filePath = System.getProperty("user.dir") + lib;    
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));   
			BufferedReader bfl = new BufferedReader(new InputStreamReader(in, "gb2312"));
            String lineString;
            String first = "一";
            int count = 0;//统计首字母相同词的个数
            int i = 0;
            int maxLength = 0;//首字母相同词中的最大词长度
            groupStart.put(first, 0);
            while ((lineString = bfl.readLine()) != null) {
                String temp = lineString.substring(0, 1);
                if (!temp.equals(first)) {//不是同首字母词
                    groupLength.put(first, count);//存储首字母相同词的个数
                    groupMaxLength.put(first, maxLength);//存储首字母相同词的最大词长度
                    groupStart.put(temp, i);//存储该类词的开始下表
                    //重置相关变量
                    maxLength = 0;
                    count = 1;
                    first = temp;
                } else//还是同首字母词
                {
                    count++;
                }
                int index = lineString.indexOf(" ");
                if (index != -1) {
                    lineString = lineString.substring(0, index);
                }
                if (maxLength < lineString.length())
                {
                    maxLength = lineString.length();//更新同类词中的最大词长度
                }
                dictionary.add(i++, lineString);//将该词存在词典中
            }
            groupLength.put(first, count);
            groupMaxLength.put(first, maxLength);
            bfl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 判断是否为中文词
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
    * 二分法查找该词在词典中的位置
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
     * 分词
    */
    public ArrayList<String> segmentation(String source) {
        ArrayList<String> seg = new ArrayList<String>();
        for (int i = 0; i < source.length(); i++) {
            String temp = source.substring(i, i + 1);
            int front = i;
            //如果不是汉字,不进行处理
            if (!isCHWord(temp.charAt(0))) {
                //检查i的大小是否超过字符串长度
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
            // 最大匹配原则查找词组
            int j;
            if (groupMaxLength.get(temp) != null) { //词典中有该词组
                for (j = groupMaxLength.get(temp); j >= 1; j--) {
                    String w;
                    if ((i + j) >= source.length()) {
                        w = source.substring(i).trim();
                    } else {
                        w = source.substring(i, i + j).trim();
                    }
                    int previous;
                    if ((previous = BiSearch(w)) < 0)//如果没有该长度的，继续查找更短长度的
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
            } else //找不到就将单个字作为一个分割
            {
                seg.add(temp);
            }
        }
        return seg;
    }

}
