package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleTypeInfo {
	
	private static Map<String,List<String>> brandtypemap = new HashMap<>();
	private static String path = "dicts/brand_models.txt";
	public static String typeExtract(String title, String brand){
		
		buildmap();
		return findtype(title,brand);
		
	}

	
	private static String findtype(String t, String b) {
		
		if(!t.contains(b)){
//			System.out.println("标题与品牌不符");
//			return "NULL";
		}
		String tt = t.replace(" ", "");
		List<String> types = brandtypemap.get(b);
		int maxlength = 0;
		if(types==null){
			return b;
		}
		for(String type : types){
			int len = type.length();
			if(len>maxlength){
				maxlength = len;
			}
		}
//		System.out.println(maxlength);
		int wsize = maxlength;
		int offe = 0;
		String temp = new String();
		while(offe<tt.length()){
			for(int i=offe+wsize-1;i>=offe;i--){
				if(i+1>tt.length()){
					continue;
				}
				temp = tt.substring(offe,i+1);
				for(String type : types){
					if(temp.toUpperCase().equals(type.toUpperCase().trim())){
//						System.out.println(temp);
						offe = i+1 ;
						return type;
					}
				}
			}
			offe++;
		}
		return b;
	}

	private static void buildmap() {
		String str = new String();
		try {
			str = IoUtil.readTxt(path);
		} catch (Exception e1) {
			System.out.println("输入文件路径异常");
		}
		str = str.replace("\r\n", "\n");
		str = str.replace("\"", "");
		str = str.replace("[", "");
		str = str.replace("]", "");
		String[] s1 = str.split("\n");
		for(String s : s1){
			String[] ss = s.split("\t");
			try{
				String brands = ss[1];
//				brands = brands.substring(1, brands.length()-1);
				brandtypemap.put(ss[0], toList(brands));
			}catch(Exception e){
				System.out.println("输入文件格式异常");
			}
		}
		
	}

	// ;隔开。品牌列表
	private static List<String> toList(String brands) {
		
		List<String> reslist = new ArrayList<>();
//		String[] ss = brands.split(";");
		String[] ss = brands.split("\002");
		for(String s : ss){
			reslist.add(s);
		}
		return reslist;
	}

}
