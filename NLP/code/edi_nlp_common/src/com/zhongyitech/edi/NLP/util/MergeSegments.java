package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;

public class MergeSegments {

	private static List<Term> seglist ;
	
	
	public static List<Term> mergeEnNum(List<Term> list){
		seglist = list;
		List<Term> res = new ArrayList<>();
		Integer flag = 0;
		String[] ss = new String[2];
		Integer start = -1;
		Integer end = -1;
		for(int i = 0; i<seglist.size(); i++){
			Term t = seglist.get(i);
			ss[0]=ss[1];
			ss[1]=t.getNatureStr();
			if(isNum(t.getName()) || isEn(t.getName())){
//			if(ss[1].equals("en") || ss[1].equals("m")){
				if(flag == 0){
					start = i;
					flag = 1;
				}
				if(i==seglist.size()-1){
					flag = 0;
					end = i;
					// 合并连续的数字和英文
					if(end-start==0){
						if(seglist.get(start).getName()==" "){
							continue;
						}else{
							res.add(seglist.get(end));
						}
					}else{
						res.add(mergeSegs(start,end));
					}
				}
				continue;
			}else if(flag==1 && (!isEn(t.getName()) && !isNum(t.getName())) ){
				flag = 0;
				end = i-1;
				// 合并连续的数字和英文
				if(end-start==0){
					if(seglist.get(start).getName()==" "){
						continue;
					}else{
						res.add(seglist.get(end));
					}
				}else{
					res.add(mergeSegs(start,end));
				}
				res.add(t);
				continue;
			}
			res.add(t);
		}
		
		return res;
	}
	
	private static boolean isEn(String str) {
		boolean b1 ;
		boolean b2 ;
		boolean res = true;
		if(str.length()==1 && Integer.valueOf(str.charAt(0))==32)
			return true;
		for(int i=0;i<str.length();i++){
			b1 = Integer.valueOf(str.charAt(i))>=97 && Integer.valueOf(str.charAt(i))<=122;
			b2 = Integer.valueOf(str.charAt(i))>=65 && Integer.valueOf(str.charAt(i))<=90;
			
			res = res && (b1||b2);
		}
		return res;
	}

	private static boolean isNum(String str) {
		boolean b ;
		boolean res = true;
		
		for(int i=0;i<str.length();i++){
			b = Integer.valueOf(str.charAt(i))>=48 && Integer.valueOf(str.charAt(i))<=57;
			
			res = res && b;
		}
		return res;
	}

	public static List<Term> mergeNonEnNum(List<Term> list){
		seglist = list;
		List<Term> res = new ArrayList<>();
		Integer flag = 0;
		String[] ss = new String[2];
		Integer start = -1;
		Integer end = -1;
		for(int i = 0; i<seglist.size(); i++){
			Term t = seglist.get(i);
			ss[0]=ss[1];
			ss[1]=t.getNatureStr();
			if(ss[1]==null || t.getName().equals(" ")){
				continue;
			}
			
			if(isNum(t.getName())||isEn(t.getName()) || (flag == 0 && ss[1].equals("n")) ){
//			if(ss[1].equals("en") || ss[1].equals("m") ||(flag == 0 && ss[1].equals("n"))){
				if(flag == 0)
					start = i;
				flag = 1;
				if(i==seglist.size()-1){
					flag = 0;
					end = i;
					// 合并连续的数字和英文
					if(end-start==0){
						if(seglist.get(start).getName()==" "){
							continue;
						}else{
							res.add(seglist.get(end));
						}
					}else{
						res.add(mergeSegs(start,end));
					}
				}
				continue;
			}else if(flag==1 && (!isEn(t.getName()) && !isNum(t.getName())) ){
				flag = 0;
				end = i-1;
				// 合并连续的数字和英文
				if(end-start==0){
					if(seglist.get(start).getName()==" "){
						continue;
					}else{
						res.add(seglist.get(end));
					}
				}else{
					res.add(mergeSegs(start,end));
				}
				res.add(t);
				continue;
			}
			res.add(t);
		}
		
		return res;
	}

	private static Term mergeSegs(Integer start, Integer end) {
		
//		if(end-start==0){
//			if(seglist.get(start).getName()==" "){
//				return null;
//			}else{
//				return seglist.get(end);
//			}
//		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = start;i<=end;i++){
			sb.append(seglist.get(i).getName());
		}
		String str = sb.toString();
		if(Integer.valueOf(str.charAt(0))==32){
			str = str.substring(1);
		}
		
		Term res = new Term(str, seglist.get(start).getOffe(), "UserDefineMerge", 1000);
		return res;
	}
	
	
}
