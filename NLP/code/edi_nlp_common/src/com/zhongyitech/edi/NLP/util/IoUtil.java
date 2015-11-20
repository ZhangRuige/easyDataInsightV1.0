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
		String str = null;
		int flag = 1;
		while ((str = br.readLine()) != null) {
			if(flag == 1){
				str=str.substring(1);
				flag=0;
			}
			sb.append(str);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void writeToText(String a, String path) throws IOException{
		Writer w=new FileWriter(path);
		
		BufferedWriter buffWriter=new BufferedWriter(w);
		buffWriter.write(a);
		buffWriter.close();
		w.close();
	}
}
