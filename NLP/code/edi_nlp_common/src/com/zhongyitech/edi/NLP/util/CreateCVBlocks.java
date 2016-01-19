package com.zhongyitech.edi.NLP.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.zhongyitech.edi.NLP.model.CRFTag;

public class CreateCVBlocks {
	
	private static String[] dicts = 
		{
		"dicts/dict0.txt",
		"dicts/dict1.txt",
		"dicts/dict2.txt",
		"dicts/dict3.txt",
		"dicts/dict4.txt",
		"dicts/dict5.txt",
		"dicts/dict6.txt",
		"dicts/dict-1.txt"
		};
	
	// 总块数
	private static int cross_validate_k = -1;
	// 每个块的大小
	private static int block_size = 3000;
	
	private static String[] dict =null;
	
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
	public static List<String> getTrainData(String str) throws Exception{
		String string = new String();
		List<String> result = new ArrayList<String>();
		try {
			string = IoUtil.readTxt(str);
		} catch (Exception e) {
			System.out.println("请检查输入文件");
			throw e;
		}
		
		File file = new File(str);
		long flen = file.length();
		// 限制每一块的大概大小，block_size kb左右
		Integer t = (int) (flen/300/block_size)+1;
		System.out.println(t);
		cross_validate_k = t;
		
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
			result.add(toCrossValBlock(taglist));
		}
		return result;
	}
	
	private static String toCrossValBlock(List<List<CRFTag>> taglist) {
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
	
	private static String toCrossValBlock2(List<List<CRFTag>> taglist) {
		String result = new String();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<taglist.size();i++){
			int size = taglist.get(i).size();
			for(int j=0;j<size;j++){
				sb.append(taglist.get(i).get(j).getWord());
				sb.append("\t");
				sb.append("O");
				sb.append("\t");
				sb.append(taglist.get(i).get(j).getSpec_tag());
				sb.append("\r\n");
			}
		}
		result = sb.toString();
		return result;
	}

	private static void loadDicts() throws Exception{
		dict = DictMakeUtil.makeOpDict(dicts);//观点元素词典
	}
	
	private static List<CRFTag> tagComm(String str) throws Exception{
		// 分割comment和indexes
		String[] sl = str.split("\t");
		String comment = sl[0];
		String indexs = sl[1];
		
		List<String> cateDict = new ArrayList<String>();
		try {
			if(cateDict==null || dict==null){
				loadDicts();
			}
			DictMakeUtil.modifyDict(dict,"/");//分词词典
			DictMakeUtil.modifyDict(cateDict);//分词词典
		} catch (Exception e) {
			System.out.println("词典路径或分类路径错误");
			throw e;
		}
		comment = OpMiningUtil.preTreatWords(comment);
		List<Term> list = ToAnalysis.parse(comment);
		// 分割出每个index
		String[] index = indexs.split(";");
		return(tagMap(comment,list,index));
	}
	
	private static List<CRFTag> tagComm2(String str) throws Exception{
		String[] sl = str.split("\t");
		if(sl.length==1){
			return(tagMap2(sl[0],null));
		}else if(sl.length==2){
			String comment = sl[0];
			String indexs = sl[1];
			
			String[] index = indexs.split(";");
			return(tagMap2(comment,index));
		}else{
			return null;
		}
	}

	private static List<List<String>> splitData(String string) {
		String[] comments = string.split("\r\n");
		if(comments.length==1)
			comments = string.split("\n");
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
	    Integer[] array = new Integer[length];
	    for (int i = 0; i < array.length; i++) {
	        array[i] = i;
	    }
	    int x = 0, tmp = 0;
	    Random random = new Random();
	    for (int i = array.length - 1; i > 0; i--) {
	        x = random.nextInt(i+1);
	        tmp = array[i];
	        array[i] = array[x];
	        array[x] = tmp;
	    }
	    return array;
	}
	// 字，词性，标注。格式的训练集
	private static List<CRFTag> tagMap(String comm,List<Term> list,String[] index){

		
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
			// 没有"/",说明是英文标点
			if(temp==-1){
				reslist.get(itag++).setPos_tag("Sw");
			}
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
			if(st>=reslist.size()||et>=reslist.size()||st>et){
				System.out.println("第"+i+"个词的index有问题.");
				continue;
			}
			if(st==-1)
				continue;
			reslist.get(st).setSpec_tag("B");
			for(int j=st+1;j<=et;j++){
//				System.out.println(i);
//				System.out.println(j);
//				System.out.println(reslist.size());
//				System.out.println(reslist.get(j).getWord());
				reslist.get(j).setSpec_tag("I");
			}
		}
		return reslist;
	}
	
	// 字，标注
	private static List<CRFTag> tagMap2(String comm,String[] index){

		
		List<Integer> s = new ArrayList<Integer>();
		List<Integer> e = new ArrayList<Integer>();
		
		// 字
		List<CRFTag> reslist = new ArrayList<CRFTag>();
		for(int i=0;i<comm.length();i++){
			CRFTag tag = new CRFTag();
			tag.setWord(comm.substring(i, i+1));
			tag.setSpec_tag("O");
			
			reslist.add(tag);
		}
		
		// 标注
		if(index!=null){
			// 处理开始结束的index
			for(int i=0;i<index.length;i++){
				s.add(Integer.parseInt(index[i].split(",")[0]));
				e.add(Integer.parseInt(index[i].split(",")[1]));
			}
			for(int i=0;i<s.size();i++){
				int st = s.get(i);
				int et = e.get(i);
				if(st>=reslist.size()||et>=reslist.size()||st>et){
					System.out.println("第"+i+"个词的index有问题.");
					continue;
				}
				if(st==-1)
					continue;
				reslist.get(st).setSpec_tag("B");
				for(int j=st+1;j<=et;j++){
					reslist.get(j).setSpec_tag("I");
				}
			}
		}
		return reslist;
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
	
	// 输出CV分块集合
	public static Long toCVBlocksTxt(List<String> l, String string) throws Exception {
		for(Integer i=0;i<l.size();i++){
			StringBuffer path = new StringBuffer();
			path.append(string+"/block");
			String s = l.get(i);
			if(i<10)
				path.append("0");
			path.append(i.toString());
			try {
				IoUtil.writeToText(s, path.toString());
			} catch (IOException e) {
				System.out.println("CV分块文件写入异常");
				e.printStackTrace();
				throw e;
			}
		}
		String path0 = string+"/block00";
		File f = new File(path0);
		return f.length();
	}

	// 输出CV分块集合s_Old
	public static Long toCVBlocksTxts_Old(List<String> l, String string) throws Exception {
		
		Integer t = 0;
		while(t<=cross_validate_k/10){
			int j = 0;
			String folder_path = string+"/blocks_"+t.toString();
			File folder = new File(folder_path);
			if(!folder.exists())
				folder.mkdir();
			int tmp = t*10;
			for(Integer i=tmp;i<l.size();i++){
				if(j<10)
					j++;
				else
					break;
				StringBuffer path = new StringBuffer();
				path.append(folder_path+"/block");
				Integer p = i-tmp;
				String s = l.get(p);
				if(i<10)
					path.append("0");
				path.append(p.toString());
				try {
					IoUtil.writeToText(s, path.toString());
				} catch (IOException e) {
					System.out.println("CV分块文件写入异常");
					e.printStackTrace();
					throw e;
				}
			}
			t++;
		}
		String path0 = string+"/blocks_0/block00";
		File f = new File(path0);
		return f.length();
	}
	
	// 输出CV分块集合s
	public static Long toCVBlocksTxts(List<String> l, String string) throws Exception {
		
		Integer t = 0;
		while(t<cross_validate_k){
			StringBuffer path = new StringBuffer();
			path.append(string);
			path.append("/block");
			path.append("_");
			path.append(t.toString());
			String s = l.get(t);
			try {
				IoUtil.writeToText(s, path.toString());
			} catch (IOException e) {
				System.out.println("CV分块文件写入异常");
				e.printStackTrace();
				throw e;
			}
			t++;
		}
		String path0 = string+"/block_0";
		File f = new File(path0);
		return f.length();
	}
	
	// 标题训练集生成
	/*
	 * 输入：1.带index的标题文本，2.输出文件保存路径
	 * 
	 * 输出：标注后的CRF格式的文本
	 * 
	 */
	public static String getTrainData_Title(String str,String path) throws Exception{
		
		String[] ss = str.split("\r\n");
		StringBuffer sb = new StringBuffer();
		String result = new String();
		for(String s :ss){
			sb.append(tagTitle(s));
		}
		result = sb.toString();
		IoUtil.writeToText(result, path);
		
		return result;
	}
	private static String tagTitle(String str) throws Exception{
		
		List<CRFTag> tag = tagComm2(str);
		List<List<CRFTag>> taglist = new ArrayList<>();
		taglist.add(tag);
		
		return toCrossValBlock2(taglist);
	}
	//转换成文本+index的类型
	public static String markTitle(String titleInfo_path, String brand_path) {
		
		String result = new String();
		StringBuffer sb = new StringBuffer();
		String title = new String();
		String brand = new String();
		try {
			title = IoUtil.readTxt(titleInfo_path);
			title = title.replace(" ", "");
			title = title.replace("\n", "。\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			brand = IoUtil.readTxt(brand_path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] brand_list0 = brand.split("\n");
		String[][] brand_list = new String[brand_list0.length][2];
		int i = 0;
		for(String s : brand_list0){
			brand_list[i][0] = s.split("\t")[0];
			brand_list[i][1] = s.split("\t")[1];
			i++;
		}
		String[] title_list = title.split("\n");
		for(String s : title_list){
			int flag = 0;
			for(String[] ss : brand_list){
				String ti = titleIndex(s,ss[0],ss[1]);
				if(ti!=null && !ti.equals(s)){
					sb.append(ti);
					if(flag==0)
						flag=1;
				}
			}
			if(flag==0)
				sb.append(s);
		}
		result = sb.toString();
		return result;
	}

	private static String titleIndex(String title, String brand, String model) {
		// TODO Auto-generated method stub
		String result = new String();
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if(model!=null && model.length()>0 && (index = title.indexOf(model))!=-1){
			sb.append(title);
			sb.append("\t");
			sb.append(index);
			sb.append(",");
			sb.append(index+model.length()-1);
			sb.append("\r\n");
			
			result = sb.toString();
		}else{
			result = title;
		}
		return result;
	}
	
}
