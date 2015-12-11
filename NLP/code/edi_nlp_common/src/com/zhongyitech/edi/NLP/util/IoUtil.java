package com.zhongyitech.edi.NLP.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

public class IoUtil {
	public static String readTxt(String path) throws Exception{
		InputStreamReader read = new InputStreamReader(new FileInputStream(path), "utf-8");
		BufferedReader br = new BufferedReader(read);
		StringBuffer sb = new StringBuffer();
		int flag = 0;
		String str = null;
		while ((str = br.readLine()) != null) {
			if(str.length()>0){
				if(flag == 0 && (Integer.valueOf(str.charAt(0))==65279 || Integer.valueOf(str.charAt(0))==32)){
					str=str.substring(1);
				}
			}
			sb.append(str);
			sb.append("\n");
			if(flag==0)
				flag=1;
		}
		br.close();
		return sb.toString();
	}
	
	public static String readTxt(String path,Integer start,Integer end) throws Exception{
		InputStreamReader read = new InputStreamReader(new FileInputStream(path), "utf-8");
		BufferedReader br = new BufferedReader(read);
		StringBuffer sb = new StringBuffer();
		int flag = 0;
		String str = null;
		int i = 0;
		while ((str = br.readLine()) != null) {
			if(i<start-1){
				i++;
				continue;
			}
			else if (i>end)
				break;
			else{
				if(str.length()>0){
					if(flag == 0 && (Integer.valueOf(str.charAt(0))==65279 || Integer.valueOf(str.charAt(0))==32)){
						str=str.substring(1);
					}
				}
				sb.append(str);
				sb.append("\n");
				i++;
				if(flag==0)
					flag=1;
			}
		}
		br.close();
		return sb.toString();
	}
	
	public static int getLines(String path) throws Exception{
		InputStreamReader read = new InputStreamReader(new FileInputStream(path), "utf-8");
		BufferedReader br = new BufferedReader(read);
		String str = null;
		int i = 0;
		while ((str = br.readLine()) != null) {
			i++;
		}
		br.close();
		return i;
	}
	
	public static void writeToText(String a, String path) throws IOException{
		Writer w=new FileWriter(path);
		
		BufferedWriter buffWriter=new BufferedWriter(w);
		buffWriter.write(a);
		buffWriter.close();
		w.close();
	}
}
