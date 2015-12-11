package com.zhongyitech.edi.NLP.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import com.zhongyitech.edi.NLP.util.CreateCVBlocks;
import com.zhongyitech.edi.NLP.util.DictMakeUtil;
import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.NewWordsDiscovery;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;
import com.zhongyitech.edi.NLP.util.W2vUtil;

public class JavaTest {

	public static void main(String[] args) throws Exception {
		
//		List<String> l = CreateCVBlocks.getTrainData("E:\\000000_0");
////		System.out.println(l);
//		CreateCVBlocks.toCVBlocksTxt(l,"E:\\blocks");
//		
////		String l2 = CRFsUtil.getNewWords("F:\\share\\result");
//		
//		String l2 = NewWordsDiscovery.getNewWords("F:\\share\\result",(float) 0.4);
//		System.out.println(l2);
//		NewWordsDiscovery.toNewWordsTxt();
//		
//		String s = new String();
//		try {
//			s = IoUtil.readTxt("E:\\000000_0");
//		} catch (Exception e) {
//			System.out.println("请检查输入文件");
//		}
//		test(s);
		
//		String s = "\"aaa\"";
//		String s1 = s.replaceAll("\\\\", "");
//		System.out.println(s1);
		
//		String path = "E:\\000000_0";
//		String comment = IoUtil.readTxt(path);
//		List<Term> wordsegment = ToAnalysis.parse(comment);
//		System.out.println(wordsegment.toString());
		
//		StringBuffer sb = new StringBuffer();
//		for(Term t :wordsegment){
//			if(!t.toString().equals("/")&&t.toString().contains("/")){
//				try{
//				sb.append(" ");
//				sb.append(t.toString().split("/")[0]);
//				}
//				catch(Exception e){
//					System.out.println(t.toString());
//				}
//			}
//		}
//		sb.deleteCharAt(0);
//		IoUtil.writeToText(sb.toString(), "E:\\WordSegment");
		
//		W2vUtil.word2vecModelTrain("");
		
//		String path = "corpus/good";
//		
//		List<Term> list1 = ToAnalysis.parse(IoUtil.readTxt(path));//分词
//		
//		OpMiningUtil.setStopWord("dicts/stopWordDict.txt");//停用词典
//		
//		List<Term> list = FilterModifWord.modifResult(list1);//去停用词
//		
//		StringBuffer s = new StringBuffer();
//		for(int i=0;i<list.size();i++){
//			s.append(list.get(i).toString()+" ");
//		}
//	
//		path = "corpus/pos";
//		String str = s.toString().replaceAll("[a-zA-Z]+", "");
//		str = str.replaceAll("[0-9]+.", "");
//		str = str.replaceAll("[！@#￥%……&*（）？：“；”‘\"'’，。,.^()<>/]", "");
//		str = str.replaceAll(" +", " ");
//		IoUtil.writeToText(str, path);
//		
//		String path2 = "corpus/bad";
//		
//		List<Term> list12 = ToAnalysis.parse(IoUtil.readTxt(path2));//分词
//		
//		OpMiningUtil.setStopWord("dicts/stopWordDict.txt");//停用词典
//		
//		List<Term> list2 = FilterModifWord.modifResult(list12);//去停用词
//		
//		StringBuffer s2 = new StringBuffer();
//		for(int i=0;i<list2.size();i++){
//			s2.append(list2.get(i).toString()+" ");
//		}
//	
//		path2 = "corpus/neg";
//		String str2 = s2.toString().replaceAll("[a-zA-Z]+", "");
//		str2 = str2.replaceAll("[0-9]+.", "");
//		str2 = str2.replaceAll("[！@#￥%……&*（）？：“；”‘\"'’，。,.^()<>/]", "");
//		str2 = str2.replaceAll(" +", " ");
//		IoUtil.writeToText(str2, path2);
		
//		List<Term> list1 = ToAnalysis.parse("CPU");
//		OpMiningUtil.setStopWord("dicts/chinese_stopword.txt");//停用词典
//		List<Term> list = FilterModifWord.modifResult(list1);//去停用词
//		System.out.println(list.toString().substring(1, list.toString().length()-1).replaceAll("/[a-zA-Z]*,*", ""));
		
		System.out.println("\\啊".replaceAll("\\\\", ""));
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	private static String[] dicts = {"dicts/dict0.txt","dicts/dict1.txt","dicts/dict2.txt","dicts/dict3.txt","dicts/dict4.txt","dicts/dict5.txt"};
	private static String category = "dicts/categoryDicts.txt";
	
	private static void test(String comm){
		
		StringBuffer sb = new StringBuffer();
		comm = comm.replaceAll("\r", "");
		String[] str1 = comm.split("\n");
		for(String s : str1){
			sb.append(s.split("\t")[0].substring(5));
			sb.append("\n");
		}
		
		List<String> cateDict = new ArrayList<String>();
		try {
			cateDict = DictMakeUtil.makeCateDict(category);//观点分类词典读取
			String[] dict = DictMakeUtil.makeOpDict(dicts);//观点元素词典
			DictMakeUtil.modifyDict(dict,"/");//分词词典
			DictMakeUtil.modifyDict(cateDict);//分词词典
		} catch (Exception e) {
			System.out.println("词典路径或分类路径错误");
		}
		
		String s1 = OpMiningUtil.preTreatWords(sb.toString());
		List<Term> list = ToAnalysis.parse(s1);
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(Term t :list){
			String w = t.toString();
			String[] ws = w.split("/");
			if(ws.length<1)
				continue;
			if(ws.length==2 && (ws[1].contains("n")||ws[1].contains("sssssser"))){
				if(map.get(ws[0])!=null){
					map.put(ws[0], map.get(ws[0])+1);
				}else{
					map.put(ws[0], 1);
				}
			}
		}

		int n = map.size();
		String[][] wordsf = new String[2][n];
		int t = 0;
		sb=new StringBuffer();
		for(Entry<String, Integer> e :map.entrySet()){
			wordsf[0][t]=e.getKey();
			wordsf[1][t]=e.getValue().toString();
			t++;
		}
		
		for(int i = 0;i<n;i++){
			for(int j = n-1;j>0;j--){
				if(Integer.parseInt(wordsf[1][j])>Integer.parseInt(wordsf[1][j-1])){
					String temp = wordsf[1][j];
					wordsf[1][j] = wordsf[1][j-1];
					wordsf[1][j-1] = temp;
					temp = wordsf[0][j];
					wordsf[0][j] = wordsf[0][j-1];
					wordsf[0][j-1] = temp;
				}
			}
		}
		
		for(int i = 0; i<n;i++){
			if(true){
				sb.append(wordsf[0][i]);
				sb.append("\t");
				sb.append(wordsf[1][i]);
				sb.append("\r\n");
			}
		}
		
		try {
			IoUtil.writeToText(sb.toString(), "E:\\rrrrrrr");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
