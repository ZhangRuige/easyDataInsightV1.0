package com.zhongyitech.edi.NLP.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ansj.vec.Word2VEC;

public class NewWordsDiscovery {
	
	private static String[] dicts = {"dicts/dict0.txt","dicts/dict1.txt","dicts/dict2.txt","dicts/dict3.txt","dicts/dict4.txt","dicts/dict5.txt"};
	private static String category = "dicts/categoryDicts.txt";
	
	private static String candi_path = "dicts/candi.txt";
	private static String nws_path = "dicts/newwords.txt";
	
	private static String crf_result = "";
	
	// 相似度阈值
	private static float sim = (float) 0.3;
	
	private static Word2VEC w2v = new Word2VEC();
	
	// 从结果中提取新词并过滤
	public static String getNewWords(String crfresult, float f) throws Exception{
		sim=f;
		return getNewWords(crfresult);
	}
	
	public static String getNewWords(String crfresult) throws Exception{
		String result = new String();
		StringBuffer sb = new StringBuffer();
		
		try {
			w2v.loadJavaModel("model/vector.mod");
		} catch (IOException e) {
			System.out.println("word2vec模型文件错误");
		}

		crf_result = crfresult;
		File f = new File(crf_result);
		if(f.isDirectory()){
			String[] fn = f.list();
			if(fn==null){
				System.out.println("找不到crf标注结果");
				return "";
			}else if(fn.length==0){
				System.out.println("找不到crf标注结果");
				return "";
			}
			
			for(String path : fn){
				List<String> cnws = findCandidateWords(path,"d");
				List<String> nws = filterWords(cnws);
				sb.append(toNewWordsString(nws));
//				Map<String,Integer> map = toNewWordsMap(sb.toString());
			}
		}else if(f.isFile()){
			List<String> cnws = findCandidateWords(crf_result,"f");
			List<String> nws = filterWords(cnws);
			sb.append(toNewWordsString(nws));
		}
		
		try {
			result = sb.toString();
			IoUtil.writeToText(result, candi_path);
		} catch (IOException e) {
			System.out.println("写入新词文件错误");
			throw e;
		}
//		return result.toString();
		return "已生成新词词典！";
	}

//	private static Map<String, Integer> toNewWordsMap(String string) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	private static List<String> findCandidateWords(String crfresult, String type) throws Exception {

		List<String> result = new CopyOnWriteArrayList<String>();
		try {
			if(type=="d")
				crfresult =IoUtil.readTxt(crf_result+"/"+crfresult);
			else if(type=="f")
				crfresult =IoUtil.readTxt(crfresult);
			crfresult = crfresult.replaceAll("\n\t", "\t");
			
			String[] w = crfresult.split("\r\n");
			if(w.length==1){
				w = crfresult.split("\n");
			}
			StringBuffer sb = new StringBuffer();
			int flag = -1;
			for(int i=0;i<w.length;i++){
				String[] sw = w[i].split("\t");
//				if(sw.length==3){
					switch(sw[3]){
					case "B":
						if(sb.length()>0 && flag == 1){
							result.add(sb.toString());
							sb = new StringBuffer();
							flag = -1;
						}
						if(sw[2]!="B")
							flag=1;
						sb.append(sw[0]);
						if(i==w.length && flag == 1){
							result.add(sb.toString());
						}
						break;
					case "I":
						if(sw[2]!="I")
							flag=1;
						sb.append(sw[0]);
						if(i==w.length){
							result.add(sb.toString());
						}
						break;
					case "O":
						if(sb.length()>0 && flag == 1){
							result.add(sb.toString());
							sb = new StringBuffer();
							flag = -1;
						}
					}
//				}
//				else if(sw.length==2){
//					switch(sw[2]){
//					case "B":
//						if(sb.length()>0){
//							result.add(sb.toString());
//							sb = new StringBuffer();
//						}
//						sb.append(sw[0]);
//						if(i==w.length){
//							result.add(sb.toString());
//						}
//						break;
//					case "I":
//						sb.append(sw[0]);
//						if(i==w.length){
//							result.add(sb.toString());
//						}
//						break;
//					case "O":
//						if(sb.length()>0){
//							result.add(sb.toString());
//							sb = new StringBuffer();
//						}
//					}
//				}else{
//					System.out.println("测试集文件格式错误");
//					return null;
//				}
			}
		} catch (Exception e) {
			System.out.println("测试集文件格式错误");
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	private static List<String> filterWords(List<String> cnws) throws Exception{
		if(cnws==null)
			return null;
		try{
			for(String s :cnws){
				if(!isNewWord(s)){
					cnws.remove(s);
				}
			}
		}catch(Exception e){
			System.out.println("还是不能直接编辑！！！！！");
			throw e;
		}
		return cnws;
	}

	private static boolean isNewWord(String s) throws Exception {

		List<String> cateDict = DictMakeUtil.makeCateDict(category);
		
	    float max = 0;
	    float temp = 0;
		for(String c : cateDict){
			try{
				temp = W2vUtil.dist(w2v.getWordVector(c), w2v.getWordVector(s));
				max = temp>max?temp:max;
			}catch(Exception e){
				continue;
			}

		}
		return max>sim;
	}

	private static String toNewWordsString(List<String> nws) {

		if(nws==null)
			return null;
		
		StringBuffer sb = new StringBuffer();
		for(String nw : nws){
			sb.append(nw);
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	
	
	
	
//	// 交叉验证列表
//	public static List<List<CRFTag>> divDataset(List<CRFTag> totalTag,int type){
//		// 切成k份，每份是一个List
//		List<List<CRFTag>> reslist = new ArrayList<List<CRFTag>>();
//		// 0顺序  1随机
//		switch(type){
//			case 0:
//				// 每份的词数
//				int n = totalTag.size()%cross_validate_k==0 ? totalTag.size()/cross_validate_k : totalTag.size()/cross_validate_k-1;
//				int index = 0;
//				while(index+n<totalTag.size()){
//					List<CRFTag> templist = new ArrayList<CRFTag>();
//					templist.addAll(totalTag.subList(index, index+n));
//					reslist.add(templist);
//					index+=n;
//				}
//				List<CRFTag> templist = new ArrayList<CRFTag>();
//				templist.addAll(totalTag.subList(index, totalTag.size()));
//				reslist.add(templist);
//				break;
//			case 1:
//				break;
//		}
//		
//		return reslist;
//	}
	
	
	
	
	
//	// 生成CRF训练文本
//	public static void makeCRFsTrainSet(List<Opinion> oplist, String w, List<Term> list) throws Exception{
//		
//		StringBuffer output = new StringBuffer();
//		String path = "corpus/CRFsTrainSet.data";
//		List<CRFTag> words = new ArrayList<CRFTag>();
////		标注专属词
//		for(int i=0;i<w.length();i++){
//			CRFTag sw = new CRFTag();
//			sw.setWord(w.substring(i,i+1));
//			for(int j = 0;j<oplist.size();j++){
//				if(i<oplist.get(j).getAspect().getStart_index()){
//					sw.setSpec_tag("O");
//					break;
//				}else if(i==oplist.get(j).getAspect().getStart_index()){
//					sw.setSpec_tag("B");
//					break;
//				}
//				else if(i>oplist.get(j).getAspect().getStart_index()&&i<=oplist.get(j).getAspect().getEnd_index()){
//					sw.setSpec_tag("I");
//					break;
//				}
//				if(sw.getSpec_tag()==null){
//					sw.setSpec_tag("O");
//				}
//			}
//			
//			words.add(sw);
//		}
//		
////		标注词性
//		int itag = 0;
//		for(int j=0;j<list.size();j++){
//			String tempstr = list.get(j).toString();
//			int temp = tempstr.indexOf("/");
//			for(int i=0;i<temp;i++){
//				if(temp==1){
//					words.get(itag++).setPos_tag("S"+tempstr.substring(temp+1));
//				}else{
//					if(i==0){
//						words.get(itag++).setPos_tag("B"+tempstr.substring(temp+1));
//					}else if(i==temp-1){
//						words.get(itag++).setPos_tag("E"+tempstr.substring(temp+1));
//					}else{
//						words.get(itag++).setPos_tag("M"+tempstr.substring(temp+1));
//					}
//				}
//			}
//		}
//		
//		for(int i=0;i<words.size();i++){
//			output.append(words.get(i).getWord());
//			output.append("\t");
//			output.append(words.get(i).getPos_tag());
//			output.append("\t");
//			output.append(words.get(i).getSpec_tag());
//			output.append("\r\n");
//		}
//		IoUtil.writeToText(output.toString(),path);
//		
//		return;
//	}
	
	// 输出新词文件
	public static void toNewWordsTxt() throws Exception {
		try {
			String w = IoUtil.readTxt(candi_path);
			new File(candi_path).delete();
			String[] ws = w.split("\n");
			
			String d1 = IoUtil.readTxt(dicts[1]);

			Map<String, Integer> nwMap = new HashMap<String, Integer>();
			int n = 0;
			StringBuffer sb = new StringBuffer();
			for (String s : ws) {
				if (!d1.contains("/" + s + "/")) {
					if (nwMap.get(s) == null)
						n = 0;
					else
						n = nwMap.get(s);
					nwMap.put(s, n++);
				}
			}
			for (Entry<String, Integer> e : nwMap.entrySet()) {
				sb.append(e.getKey());
				sb.append("/");
			}
			IoUtil.writeToText(sb.toString(), nws_path);
		} catch (Exception e) {
			System.out.println("写入词典发生异常");
			throw e;
		}
	}
}
