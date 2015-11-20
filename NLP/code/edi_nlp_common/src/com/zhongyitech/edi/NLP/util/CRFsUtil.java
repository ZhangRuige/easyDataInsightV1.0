package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.ansj.vec.Word2VEC;
import com.zhongyitech.edi.NLP.model.CRFTag;

public class CRFsUtil {
	
	private static String[] dicts = {"dicts/dict0.txt","dicts/dict1.txt","dicts/dict2.txt","dicts/dict3.txt","dicts/dict4.txt","dicts/dict5.txt"};
	private static String category = "dicts/categoryDicts.txt";
	
	private static int cross_validate_k = 10;
	// 相似度阈值
	private static float sim = (float) 0.35 ;
	
	/* CRF过程
	 * 
	 * 1.读数据,分词
	 * 2.标注数据
	 * 3.分割数据
	 * -------crf--------
	 * 4.训练90%    生成model，shell形式
	 * 5.计算10%
	 * 6.发现结果
	 * 7.重复4-6，10次
	 * =======crf========
	 * 8.筛选判断
	 * 9.更新词典
	 */
	
	// 分割数据形成训练集
	public static List<String> getTrainData(String string) throws Exception{
		List<String> result = new ArrayList<String>();
		
		// 切分成交叉验证的块
		List<List<String>> list = splitData(string);
		// 每个块包含多条评论
		for(List<String> comments :list){
			List<List<CRFTag>> taglist = new ArrayList<List<CRFTag>>();
			// 对每条评论和index
			for(String comment :comments){
				// 标注
				taglist.add(tagComm(comment));
			}
			// 每块标注to字符串
			result.add(toTrainSet(taglist));
		}
		return result;
	}
	
	private static String toTrainSet(List<List<CRFTag>> taglist) {
		String result = new String();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<taglist.size();i++){
			int size = taglist.get(i).size();
			for(int j=0;j<size;j++){
				sb.append(taglist.get(i).get(j).getWord());
				sb.append("\t");
				sb.append(taglist.get(i).get(j).getPos_tag());
				sb.append("\t");
				sb.append(taglist.get(i).get(j).getSpec_tag());
				sb.append("\r\n");
			}
		}
		result = sb.toString();
		return result;
	}

	private static List<CRFTag> tagComm(String str) throws Exception {
		// 分割comment和indexes
		String[] sl = str.split("\t");
		String comment = sl[0];
		String indexs = sl[1];
		
		List<String> cateDict = DictMakeUtil.makeCateDict(category);//观点分类词典读取
		String[] dict = DictMakeUtil.makeOpDict(dicts);//观点元素词典
		DictMakeUtil.modifyDict(dict,"/");//分词词典
		DictMakeUtil.modifyDict(cateDict);//分词词典
		
		comment = OpMiningUtil.preTreatWords(comment);
		List<Term> list = ToAnalysis.parse(comment);
		// 分割出每个index
		String[] index = indexs.split(";");
		return(tagMap(comment,list,index));
	}

	private static List<List<String>> splitData(String string) {
		String[] comments = string.split("\r\n");
		Integer[] comlen = new Integer[comments.length];
		for(int i=0;i<comments.length;i++){
			comlen[i] = comments[i].indexOf("\t");
		}
		// 分块方法
		List<List<Integer>> sp = splitComments(comlen);
		// 按分块序号构造结果列表
		List<List<String>> result = toSplitList(comments,sp);
		return result;
	}

	private static List<List<String>> toSplitList(String[] comments, List<List<Integer>> sp) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		for(int i=0;i<sp.size();i++){
			List<Integer> ids = sp.get(i);
			List<String> list = new ArrayList<String>();
			for(int j=0;j<ids.size();j++){
				list.add(comments[ids.get(j)]);
			}
			result.add(list);
		}
		return result;
	}

	private static List<List<Integer>> splitComments(Integer[] comlen) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		int len = 0;
		for(Integer l:comlen){
			len+=l;
		}
		len = len%cross_validate_k==0 ? len/cross_validate_k : len/cross_validate_k-1;
		Integer[] r = randomList(comlen.length);
		int indexr = 0;
		for(int i = 0;i<cross_validate_k;i++){
			int templen = 0;
			List<Integer> tl = new ArrayList<Integer>();
			while(templen<len && indexr<r.length){
				tl.add(r[indexr]);
				templen+=comlen[r[indexr]];
				indexr++;
			}
			result.add(tl);
		}
		return result;
	}

	// 对某条评论贴标签列表
	private static Integer[] randomList(int length) {
		Integer[] result = new Integer[length];
		for(int i=0;i<length;i++){
			result[i] = i;
		}
		return result;
	}

	public static List<CRFTag> tagMap(String comm,List<Term> list,String[] index) throws Exception{

		
		List<Integer> s = new ArrayList<Integer>();
		List<Integer> e = new ArrayList<Integer>();
		// 处理开始结束的index
		for(int i=0;i<index.length;i++){
			s.add(Integer.parseInt(index[i].split(",")[0]));
			e.add(Integer.parseInt(index[i].split(",")[1]));
		}
		
		// 字
		List<CRFTag> reslist = new ArrayList<CRFTag>();
		for(int i=0;i<comm.length();i++){
			CRFTag tag = new CRFTag();
			tag.setWord(comm.substring(i, i+1));
			tag.setSpec_tag("O");
			
			reslist.add(tag);
		}
		
		// 词性
		int itag = 0;
		for(int j=0;j<list.size();j++){
			String tempstr = list.get(j).toString();
			int temp = tempstr.indexOf("/");
			for(int i=0;i<temp;i++){
				if(temp==1){
					reslist.get(itag++).setPos_tag("S"+tempstr.substring(temp+1));
				}else{
					if(i==0){
						reslist.get(itag++).setPos_tag("B"+tempstr.substring(temp+1));
					}else if(i==temp-1){
						reslist.get(itag++).setPos_tag("E"+tempstr.substring(temp+1));
					}else{
						reslist.get(itag++).setPos_tag("M"+tempstr.substring(temp+1));
					}
				}
			}
		}
		
		// 标注
		for(int i=0;i<s.size();i++){
			int st = s.get(i);
			int et = e.get(i);
			reslist.get(st).setSpec_tag("B");
			for(int j=st+1;j<=et;j++){
				reslist.get(j).setSpec_tag("I");
			}
		}
		
		return reslist;
	}
	
	// 从结果中提取新词并过滤
	public static String getNewWords(String crfresult) throws Exception{
		
		List<String> cnws = findCandidateWords(crfresult);
		List<String> nws = filterWords(cnws);
		String result = toNewWordsString(nws);
		
		return result;
	}

	private static List<String> findCandidateWords(String crfresult) {

		List<String> result = new ArrayList<String>();
		
		String[] w = crfresult.split("\r\n");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<w.length;i++){
			String[] sw = w[i].split("\t");
			switch(sw[2]){
				case "B":
					if(sb.length()>0){
						result.add(sb.toString());
						sb = new StringBuffer();
					}
					sb.append(sw[0]);
					if(i==w.length){
						result.add(sb.toString());
					}
					break;
				case "I":
					sb.append(sw[0]);
					if(i==w.length){
						result.add(sb.toString());
					}
					break;
				case "O":
					if(sb.length()>0){
						result.add(sb.toString());
						sb = new StringBuffer();
					}
			}
		}
		
		return result;
	}

	private static List<String> filterWords(List<String> cnws) throws Exception {
		if(cnws==null)
			return null;
		try{
			for(String s :cnws){
				if(!isNewWord(s)){
					cnws.remove(s);
				}
			}
		}catch(Exception e){
			if(e.toString().contains("ConcurrentModificationException"))
				return null;
		}
		return cnws;
	}

	private static boolean isNewWord(String s) throws Exception {

		List<String> cateDict = DictMakeUtil.makeCateDict(category);
		
		Word2VEC w2v = new Word2VEC();
	    w2v.loadJavaModel("model/vector.mod");
		
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

}
