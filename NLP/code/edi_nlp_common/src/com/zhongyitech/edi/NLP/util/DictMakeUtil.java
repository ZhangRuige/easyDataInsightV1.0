package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.List;

import org.ansj.library.UserDefineLibrary;

public class DictMakeUtil {

	public static void modifyDict(String[] dict, String split) {
		
		for(String s :dict){
			String[] ss = s.split(split);
			for(int i=1;i<ss.length;i++){
				UserDefineLibrary.insertWord(ss[i], "userDefine", 1000);
			}
		}
	}
	
	public static void modifyDict(String[] dict) {
		
		for(String s :dict){
			UserDefineLibrary.insertWord(s, "userDefine", 1000);
		}
	}
	
	public static void modifyDict(List<String> dict) {
		
		for(String s :dict){
			UserDefineLibrary.insertWord(s, "userDefine", 1000);
		}
	}
	
	public static String[] makeOpDict(String[] dicts) throws Exception {
		
		String[] result = new String[dicts.length];
		int i = 0;
		for(String dict : dicts){
			result[i++]=IoUtil.readTxt(dict);
		}
		return result;
		
	}

	public static List<String> makeCateDict(String category) throws Exception {
		
		List<String> resultlist = new ArrayList<String>();
		
		String c = IoUtil.readTxt(category);
		String[] cs = c.split(" ");
		for(String cc : cs){
			if(cc.contains("\n"))
				resultlist.add(cc.replaceAll("\n", ""));
			else
				resultlist.add(cc);
		}
		return resultlist;
	}

	
}
