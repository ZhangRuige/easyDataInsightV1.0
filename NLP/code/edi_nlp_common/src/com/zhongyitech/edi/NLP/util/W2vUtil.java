package com.zhongyitech.edi.NLP.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;

public class W2vUtil {
	
//	private static Map<String, float[]> word2VecMap = new HashMap<String, float[]>();
//	
//	public static Map<String, float[]> setW2vMap(String w2vMap) throws Exception {
//		
//		if(word2VecMap.size()>0){
//			return word2VecMap;
//		}else{
//			InputStreamReader read = new InputStreamReader(new FileInputStream(w2vMap), "utf-8");
//			BufferedReader br = new BufferedReader(read);
//			String str = null;
//			while((str=br.readLine())!=null){
//				String[] s = str.split("\t");
//				if(s[0]==null || s.length!=2 || s[1].equals("null") || s[1]==null)
//					continue;
//				else{
//					try{
//						float[] f = toFloatArray(s[1]);
//						word2VecMap.put(s[0], f);
//					}catch (Exception e){
//						System.out.println(s[1]);
//					}
//				}
//			}
//			return word2VecMap;
//		}
//	}
//
//	private static float[] toFloatArray(String value) {
//		
//		String[] s = value.split(",");
//		float[] f = new float[s.length];
//		
//		for(int i = 0; i<s.length; i++){
//			f[i] = Float.parseFloat(s[i]);
//		}
//		
//		return f;
//	}

	public static float dist(float[] fs, float[] fs2) {
		if(fs2 == null)
//			return 99999;
			return 0;
		if(fs!=null){
//			float f = 0;
//			for(int i = 0;i<fs.length;i++){
//				f+=(fs[i]-fs2[i])*(fs[i]-fs2[i]);
//			}
//			f = (float) Math.sqrt(f);
//			return f;
			float f = 0;
			for(int i = 0;i<fs.length;i++){
				f+=fs[i]*fs2[i];
			}
			return f;
		}else{
//			return 99999;
			return 0;
		}
	}

	public static void word2vecModelTrain(String corpus) throws Exception {
		
		List<Term> list = ToAnalysis.parse(IoUtil.readTxt(corpus));
		StringBuffer s = new StringBuffer();
		for(int i=0;i<list.size();i++){
			s.append(list.get(i).toString()+" ");
		}
//		分词后的文本
		String path = "corpus/cwsFile.txt";
		String str = s.toString().replaceAll("[a-zA-Z]+", "");
		str = str.replaceAll("[0-9]+.", "");
		str = str.replaceAll("[！@#￥%……&*（）？：“；”‘\"'’，。,.^()<>/]", "");
		str = str.replaceAll(" +", " ");
		IoUtil.writeToText(str, path);
		Learn lean = new Learn();
	    lean.learnFile(new File(path));
	    lean.saveModel(new File("model/vector.mod"));
//		return result;
	}

//	// 词向量写出来,无意义
//	public static void word2vecMap(String vecPath) throws Exception{
//		String path = "corpus/cwsFile.txt";
//		String str = IoUtil.readTxt(path);
//		Word2VEC w2v = new Word2VEC();
//	    w2v.loadJavaModel("model/vector.mod");
//	    
//	    String[] words = str.split(" ");
////	    Map<String, float[]> result = new HashMap<String, float[]>();
//
//	    OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(vecPath),"UTF-8");
//		BufferedWriter bw=new BufferedWriter(w);
//	    
//	    for(int i = 0;i<words.length;i++){
//	    	StringBuffer sb = new StringBuffer();
//	    	float[] f = w2v.getWordVector(words[i]);
//	    	
//	    	if(word2VecMap.get(words[i])==null){
//		    	word2VecMap.put(words[i], f);
//		    	sb.append(words[i]);
//		    	sb.append("\t");
//		    	appendFloatArray(sb,f);
//		    	sb.append("\r\n");
//		    	bw.write(sb.toString());
//	    	}
//	    }
//	}
//	
//	private static void appendFloatArray(StringBuffer sb, float[] f) {
//		if(f!=null){
////			sb.append("[");
//			for(int i=0;i<f.length-1;i++){
//				sb.append(f[i]);
//				sb.append(",");
//			}
//			sb.append(f[f.length-1]);
////			sb.append("]");
//		}else
//			sb.append(f);
//	}

//	public static Map<String, float[]> getWord2VecMap() {
//		return word2VecMap;
//	}
//
//	public static void setWord2VecMap(Map<String, float[]> word2VecMap) {
//		W2vUtil.word2VecMap = word2VecMap;
//	}
}
