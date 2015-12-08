package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class RivalProductUtil {

	private static String brand_path ="dicts/brand.txt";
	private static String alias_path ="dicts/alias.txt";
	
	public static String findRivalProduct(String comment) {
		
		String rivalBrandDict = new String();
		String rivalAliasDict = new String();
		
		String res = new String();
		
		try {
			rivalBrandDict = IoUtil.readTxt(brand_path);
			DictMakeUtil.modifyDict(rivalBrandDict,"\n");
		} catch (Exception e) {
			System.out.println("竞品品牌词典读取失败");
			e.printStackTrace();
		}
		try {
			rivalAliasDict = IoUtil.readTxt(alias_path);
			DictMakeUtil.modifyDict(rivalAliasDict,"\n");
		} catch (Exception e) {
			System.out.println("竞品型号别名词典读取失败");
			e.printStackTrace();
		}
		
		// 这边是否要对型号单独处理
		// 因为型号通常是长句子,不容易被分词切出来
		
		rivalAliasDict="/"+rivalAliasDict+"/";
		rivalAliasDict=rivalAliasDict.replaceAll("\r", "");
		rivalAliasDict=rivalAliasDict.replaceAll("\n", "/");
		
		rivalBrandDict="/"+rivalBrandDict+"/";
		rivalBrandDict=rivalBrandDict.replaceAll("\r", "");
		rivalBrandDict=rivalBrandDict.replaceAll("\n", "/");
		
		// 对评论分词,提取品牌和型号
		List<Term> list = ToAnalysis.parse(comment);
		StringBuffer res_sb = new StringBuffer();
		
		List<String> brand_list = new ArrayList<String>();
		int j = 0;
		for(int i=0;i<list.size();i++){
			Term term = list.get(i);
			String t = new String();
			if(term.toString().length()>1 && term.toString().contains("/")){
				t = term.toString().substring(0,term.toString().indexOf("/"));
			}else{
				continue;
			}
			if(t.length()<1){
				continue;
			}
			// 超过最大间隔
			if(j>3){
				if(brand_list.size()>0){
					res_sb.append(brand_list.get(0));
					res_sb.append(",");
					res_sb.append("null");
					res_sb.append(";");
					j=0;
					brand_list.clear();
					continue;
				}
				j=0;
			}
			// 发现品牌
			if(rivalBrandDict.contains("/"+t.toString()+"/")){
				if(brand_list.size()!=0){
					res_sb.append(brand_list.get(0));
					res_sb.append(",");
					res_sb.append("null");
					res_sb.append(";");
					j=0;
					brand_list.clear();
					brand_list.add(t.toString());
					continue;
				}else{
					brand_list.add(t.toString());
					continue;
				}
			}
			// 发现型号别名
			if(rivalAliasDict.contains("/"+t.toString()+"/")){
				if(brand_list.size()!=0){
					res_sb.append(brand_list.get(0));
					res_sb.append(",");
					res_sb.append(t.toString());
					res_sb.append(";");
					j=0;
					continue;
				}else{
					res_sb.append("null");
					res_sb.append(",");
					res_sb.append(t.toString());
					res_sb.append(";");
					j=0;
					continue;
				}
			}
			j++;
		}
		
		res = res_sb.toString();
		res = elDupli(res);
		
		return res;
	}

	private static String elDupli(String str) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		String []strs = str.split(";");
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(String s :strs){
			if(map.get(s)==null)
				map.put(s, 1);
			else
				map.put(s, map.get(s)+1);
		}
		for(Entry<String,Integer> e :map.entrySet()){
			sb.append(e.getKey());
//			sb.append(",");
//			sb.append(e.getValue());
			sb.append(";");
		}
		return sb.toString();
	}

}
