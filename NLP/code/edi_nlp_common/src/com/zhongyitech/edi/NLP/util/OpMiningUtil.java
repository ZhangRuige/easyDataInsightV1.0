package com.zhongyitech.edi.NLP.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import com.ansj.vec.Word2VEC;
import com.zhongyitech.edi.NLP.model.OpAspElement;
import com.zhongyitech.edi.NLP.model.OpElement;
import com.zhongyitech.edi.NLP.model.OpSentiElement;
import com.zhongyitech.edi.NLP.model.OpTreeNode;
import com.zhongyitech.edi.NLP.model.Opinion;

public class OpMiningUtil {
	/*
	 * dicts... 0品牌词 1对象词 2属性词 3正面词 4负面词 5否定词
	 */
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
	private static String category = "dicts/categoryDicts.txt";
	private static String corpus = "corpus/w2vTrainSet.txt";
	
	private static String stpwdict = "dicts/stopWordDict.txt";
//	private static String stpwdict = "dicts/chinese_stopword.txt";
	
	private static List<String> cateDict = null;
	private static String[] dict = null;
	
	// 观点树
	private static List<OpTreeNode> opTree = new ArrayList<OpTreeNode>();
	// 产品队列
	private static List<OpElement> pro_list = new ArrayList<>();
	// 对象队列
	private static List<OpElement> asp_list = new ArrayList<>();
	// 属性队列
	private static List<OpElement> att_list = new ArrayList<>();
//	// 情感词队列
//	private static List<OpElement> sen_list = new ArrayList<>();
	
	// 每个二级分类的一级类别序号
	private static List<Integer> pri_category = new ArrayList<>();
	
	private static List<String> pri_cate_dict = new ArrayList<>();
	
	private static Word2VEC w2v = new Word2VEC();
	private static int w2vflag = 0;
	//用于标注前两个词在观点树中的层次
	private static int[] lastTermLevel = {0,0};
	
	//对象分类相似度阈值
	private static float similarity = (float) 0.35 ;
	//观点句最大间隔
	private static int maxgap = 10;
	//一、二级分类最大间隔
	private static int maxgap2 = 3;
	/* 分词种类:
	 * 1BaseAnalysis
	 * 2ToAnalysis
	 */
	private static int segType = 2;
	
	
	public static void trainModel() throws Exception{
		W2vUtil.word2vecModelTrain(corpus);
	}
	private static List<Integer> setPriCategory() {
		List<Integer> res = new ArrayList<>();
		if(w2vflag==0){
			try {
				w2v.loadJavaModel("model/vector.mod");
			} catch (IOException e) {
				System.out.println("二级属性分类发生错误，读取w2v模型失败");
				e.printStackTrace();
			}
			w2vflag=1;
		}
		try {
			loadDicts();
		} catch (Exception e1) {
			System.out.println("二级属性分类发生错误，读取二级分类词典失败");
			e1.printStackTrace();
		}
		String d1 = new String();
		try {
			d1 = IoUtil.readTxt(dicts[1]);
		} catch (Exception e) {
			System.out.println("二级属性分类发生错误，读取一级分类词典失败");
			e.printStackTrace();
		}
		String[] d1s = d1.substring(1, d1.length()-1).split("/");
		for (int i = 0; i < d1s.length; i++){
			pri_cate_dict.add(d1s[i]);
		}
		Integer category;
		float maxd;
		for (int i = 0; i < cateDict.size(); i++) {
			category = -1;
			maxd = 0;
			for(int j = 0; j<pri_cate_dict.size();j++){
				float temp = 0;
					temp = W2vUtil.dist(w2v.getWordVector(pri_cate_dict.get(j)),w2v.getWordVector(cateDict.get(i)));
				if(temp > similarity){
					boolean b = temp > maxd;
					maxd = b ? temp : maxd;
					category = b ? j : category;
				}
			}
			res.add(category);
		}
		return res;
	}
	public static void trainModel(String file) throws Exception{
		W2vUtil.word2vecModelTrain(file);
	}
	// 避免多次IO
	private static void loadDicts() throws Exception{
		cateDict = DictMakeUtil.makeCateDict(category);//观点分类词典读取
		dict = DictMakeUtil.makeOpDict(dicts);//观点元素词典
	}
	
	public static List<Opinion> doSa(String words,String product) throws Exception{
		if(words==null){
			return new ArrayList<>();
		}
		String words1 = preTreatWords(words);
		if(w2vflag==0){
			w2v.loadJavaModel("model/vector.mod");
			w2vflag=1;
		}
		if(cateDict==null || dict==null){
			loadDicts();
		}
		DictMakeUtil.modifyDict(dict,"/");//分词词典
		DictMakeUtil.modifyDict(cateDict);//分词词典
		
		pri_category = setPriCategory();
		
		List<Term> list1 = new ArrayList<Term>();
		
		switch(segType){
		case 1:
			list1 = BaseAnalysis.parse(words1);
			break;
		case 2:
			list1 = ToAnalysis.parse(words1);
			break;
		}

		setStopWord(stpwdict);
		List<Term> list = FilterModifWord.modifResult(list1);//去停用词
		List<Opinion> oplist = OpMiningUtil.opMining(words, dict ,list, product);//观点提取
		oplist = OpMiningUtil.aspectCategory(oplist);//观点对象分类,包括一级二级分类
		
//		System.out.println(oplist.size());
		
		return oplist;
	}
	
	// 去停用词
	public static void setStopWord(String stw) throws Exception {

		String str = IoUtil.readTxt(stpwdict);
		String[] ss = str.split("\n");
		for(String s : ss){
			FilterModifWord.insertStopWord(s);
		}
	}
	
	// 预处理
	public static String preTreatWords(String words) {
//		String words1 = words.replaceAll("、","#顿号#");
		String words1 = words.replaceAll(" ",".");
//		words1 = words1.replaceAll("，","#逗号#");
//		UserDefineLibrary.insertWord("#顿号#", "wn", 1000);
//		UserDefineLibrary.insertWord("#逗号#", "wn", 1000);
//		UserDefineLibrary.insertWord("#空格#", "wn", 1000);
		return words1;
	}
	
	// 观点提取
	public static List<Opinion> opMining(String w, String[] dict, List<Term> list, String p){
		
		opTreeConstruct(w,dict,list,p);
		return sentimentAnalysis(dict,opExtract(w,list,dict));
		
	}
	
	// 情感分析
	private static List<Opinion> sentimentAnalysis(String[] dict, List<Opinion> opExtract) {
		
		for(Opinion op : opExtract){
			int neg = op.getNeg_words().size()%2;
			op.getSentiment().setSentiment_category(op.getSentiment().getSentiment_value()*(neg==0?1:-1)>0?"positive":"negative");
		}
		return opExtract;
	}

	// 从观点树提取观点
	private static List<Opinion> opExtract(String w, List<Term> list, String[] dict) {
		
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			s.append(list.get(i).toString() + " ");
		}
		String[] seg = s.toString().split(" ");
		// 否定词队列
		String[] neg_words = new String[list.size()];
		for(int i = 0; i<seg.length; i++){
			String[] segsplit = seg[i].split("/");
			if (segsplit.length != 2)
				continue;
			if (dict[dict.length-1].contains("/" + segsplit[0] + "/")) {
				neg_words[i] = segsplit[0];
				continue;
			}
		}

		List<Opinion> oplist = new ArrayList<Opinion>();
		for(OpTreeNode node : opTree){
			if(!node.isLeaf())
				continue;
			Opinion op = new Opinion();
			op.setProduct(new OpElement());
			op.setAspect(new OpAspElement());
			op.setAttribute(new OpElement());
			do{
				op = setOpinion(op,node);
				node = opTree.get(node.getParents_id());
			}while(node.getNode_depth()!=0);
			
			// 保存否定词
			List<String> ngws = new ArrayList<String>();
			for(int i = op.getTerm_start_index();i<op.getTerm_end_index();i++){
				if(neg_words[i]!=null)
					ngws.add(neg_words[i]);
			}
			op.setNeg_words(ngws);
			op.setRaw_data(w);
			oplist.add(op);
		}
		return oplist;
	}
	
	private static Opinion setOpinion(Opinion op, OpTreeNode n) {
		switch (n.getNode_depth()){
			case 1:
				op.setProduct(n.getNode_element());
				break;
			case 2:
				op.setAspect((OpAspElement) n.getNode_element());
				break;
			case 3:
				op.setAttribute(n.getNode_element());
				break;
			case 4:
				op.setSentiment((OpSentiElement) n.getNode_element());
				break;
			case 5:
				op.setSentiment((OpSentiElement) n.getNode_element());
				break;
		}
		return op;
	}
	
	// 观点树构造,按照规则.
	public static List<OpTreeNode> opTreeConstruct(String w, String[] dict, List<Term> list, String p) {

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			s.append(list.get(i).toString() + " ");
		}

//		List<OpTreeNode> opTree = new ArrayList<OpTreeNode>();
		opTree.add(new OpTreeNode());
		int treeIndex = 1;
		int pro_flag = 0;
		int fromIndex = 0;
		/*改成静态
		// 产品队列
		List<OpElement> pro_list = new ArrayList<>();
		// 对象队列
		List<OpElement> asp_list = new ArrayList<>();
		// 属性队列
		List<OpElement> att_list = new ArrayList<>();
//		// 情感词队列
//		List<OpElement> sen_list = new ArrayList<>();
		*/
		String product = p;
		// 外部有指定的产品品牌型号
		if (product!=null && !product.equals("")) {
			pro_flag = -1;
			OpElement e0 = new OpElement();
			e0.setOpElementInfos(product, null, null, treeIndex);
			pro_list.add(e0);
			OpTreeNode node = new OpTreeNode(treeIndex++);
			// 父亲是根节点
			node.setNodeInfos(e0, "0", "1", null);
			opTree.add(node);
		}

		String[] seg = s.toString().split(" ");

//		for (int i = 0; i < seg.length; i++) {
//			// 发现观点中的元素
			for (int j = 0; j < seg.length; j++) {
				String[] segsplit = seg[j].split("/");
				if (segsplit.length != 2)
					continue;
				// 如果满足其他观点结束规则
				if (otherEndRule(seg[j])){
					/* 异常结束，说明没有感情词。
					 * ## 要把所有异常结束都放到这个方法里
					 * ## 必须清空所有队列
					 * ## continue,重新开始提取新观点
					 */
					continue;
				}
				
				// 否则按正常结束判断
				// 如果是产品
				if (dict[0].contains("/" + segsplit[0] + "/")) {
					if (pro_flag == -1) {
						continue;
					} else {
						// 如果都不是，清空产品队列
						if (!isPara(seg, j, 1) && !isComp()) {
							pro_list.clear();
						}
						// 如果是比較，先不处理
						if (isComp()) {
							continue;
						}
						// 新增节点并加入已有产品队列
						OpElement e = new OpElement();
						int temp = w.indexOf(segsplit[0], fromIndex);
						fromIndex = temp - 1 + segsplit[0].length();
						e.setOpElementInfos(segsplit[0], String.valueOf(temp),
								String.valueOf(fromIndex), treeIndex);
						e.setTerm_index(j);
						pro_list.add(e);
						// 清空所有更低级的队列
						asp_list.clear();
						att_list.clear();
						// 父亲是根节点
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, "0", "1", null);
						opTree.add(node);
						
						lastTermLevel[0]=lastTermLevel[1];
						lastTermLevel[1]=1;
						continue;
					}
				}
				// 是标点符号?
//				// 是对象
//				else if (dict[1].contains("/" + segsplit[0] + "/")) {
//					// 不是并列不是比较（就是新的对象）
//					if(!isPara(seg, j, 2)&&!isComp()){
//						// 清空列表
//						asp_list.clear();
//					}
//					if(isComp()){
//						continue;
//					}
//					OpElement e = new OpAspElement();
//					int temp = w.indexOf(segsplit[0], fromIndex);
//					fromIndex = temp - 1 + segsplit[0].length();
//					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
//							String.valueOf(fromIndex), treeIndex);
//					e.setTerm_index(j);
//					asp_list.add(e);
//					// 清空所有更低级的队列
//					att_list.clear();
//					if(pro_list.size()==0)
//						continue;
//					int t = 0;
//					// 对上层队列的所有元素新增孩子节点
//					while(t<pro_list.size()){
//						// 父亲是上层队列的首节点
//						int parents = pro_list.get(t++).getTree_index();
//						OpTreeNode node = new OpTreeNode(treeIndex++);
//						node.setNodeInfos(e, String.valueOf(parents), "2", null);
//						opTree.add(node);
//					}
//					
//					lastTermLevel[0]=lastTermLevel[1];
//					lastTermLevel[1]=2;
//					continue;
//				}
				// 是属性
				else if (dict[2].contains("/" + segsplit[0] + "/")) {
					// 不是并列不是比较
					if(!isPara(seg, j, 3)&&!isComp()){
						// 清空列表
						att_list.clear();
					}
					if(isComp()){
						continue;
					}
					OpAspElement e = new OpAspElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setTerm_index(j);
					att_list.add(e);
					int t = 0;
					// 父亲是上层队列的首节点
					if(asp_list.size()==0 || e.getStart_index()-asp_list.get(0).getEnd_index()> maxgap2){
						asp_list.clear();
						if(pro_list.size()==0){
							continue;
						}
						while(t<pro_list.size()){
							int parents = pro_list.get(t++).getTree_index();
							OpTreeNode node = new OpTreeNode(treeIndex++);
							node.setNodeInfos(e, String.valueOf(parents), "3", null);
							opTree.add(node);
						}
					}
						
					// 对上层队列的所有元素新增孩子节点
					while(t<asp_list.size()){
						int parents = asp_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "3", null);
						opTree.add(node);
					}
					
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=3;;
					continue;
				}
				// 是正情感
				else if (dict[3].contains("/" + segsplit[0] + "/")) {
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=4;
					// 情感是最后一层,不判断是否并列
					OpSentiElement e = new OpSentiElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setSentiment_value(1);
					e.setTerm_index(j);
//					sen_list.add(e);
					// 父亲是上层队列的首节点
					int t = 0;
					if(att_list.size()==0){
						if(asp_list.size()==0){
							if(pro_list.size()==0 || pro_flag == -1){
								continue;
							}
							// 如果和观点对象距离太远，则放弃
							if(e.getStart_index()-pro_list.get(0).getEnd_index()> maxgap)
								continue;
//							editAll(e,pro_list,"3",treeIndex,opTree);
							while(t<pro_list.size()){
								int parents = pro_list.get(t++).getTree_index();
								OpTreeNode node = new OpTreeNode(treeIndex++);
								node.setNodeInfos(e, String.valueOf(parents), "4", "true");
								opTree.add(node);
							}
							if(pro_flag!=-1){
								pro_list.clear();
							}
							continue;
						}
						// 如果和观点对象距离太远，则放弃
						if(e.getStart_index()-asp_list.get(0).getEnd_index()> maxgap)
							continue;
						while(t<asp_list.size()){
							int parents = asp_list.get(t++).getTree_index();
							OpTreeNode node = new OpTreeNode(treeIndex++);
							node.setNodeInfos(e, String.valueOf(parents), "4", "true");
							opTree.add(node);
						}
						asp_list.clear();
						continue;
					}
					// 如果和观点对象距离太远，则放弃
					if(e.getStart_index()-att_list.get(0).getEnd_index()> maxgap)
						continue;
					// 对上层队列的所有元素新增孩子节点
					while(t<att_list.size()){
						int parents = att_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "4", "true");
						opTree.add(node);
					}
					asp_list.clear();
					att_list.clear();
					continue;
				}
				//是负情感
				else if (dict[4].contains("/" + segsplit[0] + "/")) {
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=4;
					// 情感是最后一层,不判断是否并列
					OpSentiElement e = new OpSentiElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setSentiment_value(-1);
					e.setTerm_index(j);
//					sen_list.add(e);
					// 父亲是上层队列的首节点
					int t = 0;
					if(att_list.size()==0){
						if(asp_list.size()==0){
							if(pro_list.size()==0 || pro_flag == -1){
								continue;
							}
//							editAll(e,pro_list,"3",treeIndex,opTree);
							// 如果和观点对象距离太远，则放弃
							if(e.getStart_index()-pro_list.get(0).getEnd_index()> maxgap)
								continue;
							while(t<pro_list.size()){
								int parents = pro_list.get(t++).getTree_index();
								OpTreeNode node = new OpTreeNode(treeIndex++);
								node.setNodeInfos(e, String.valueOf(parents), "4", "true");
								opTree.add(node);
							}
							if(pro_flag!=-1){
								pro_list.clear();
							}
							continue;
						}
						// 如果和观点对象距离太远，则放弃
						if(e.getStart_index()-asp_list.get(0).getEnd_index()>maxgap)
							continue;
						while(t<asp_list.size()){
							int parents = asp_list.get(t++).getTree_index();
							OpTreeNode node = new OpTreeNode(treeIndex++);
							node.setNodeInfos(e, String.valueOf(parents), "4", "true");
							opTree.add(node);
						}
						asp_list.clear();
						continue;
					}
					// 如果和观点对象距离太远，则放弃
					if(e.getStart_index()-att_list.get(0).getEnd_index()>maxgap)
						continue;
					// 对上层队列的所有元素新增孩子节点
					while(t<att_list.size()){
						int parents = att_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "4", "true");
						opTree.add(node);
					}
					asp_list.clear();
					att_list.clear();
					continue;
				}
				//是单独正观点词
				else if (dict[5].contains("/" + segsplit[0] + "/")) {
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=4;
					// 情感是最后一层,不判断是否并列
					OpSentiElement e = new OpSentiElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setSentiment_value(1);
					e.setTerm_index(j);
//					sen_list.add(e);
					// 父亲是上层队列的首节点
					int t = 0;
//					editAll(e,pro_list,"3",treeIndex,opTree);
					// 如果和观点对象距离太远，则放弃
					if(pro_list.get(0).getEnd_index()!=-1 && e.getStart_index()-pro_list.get(0).getEnd_index()> maxgap)
						continue;
					while(t<pro_list.size()){
						int parents = pro_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "5", "true");
						opTree.add(node);
					}
					if(pro_flag!=-1){
						pro_list.clear();
					}
					asp_list.clear();
					att_list.clear();
					continue;
				}
				//是单独负观点词
				else if (dict[6].contains("/" + segsplit[0] + "/")) {
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=4;
					// 情感是最后一层,不判断是否并列
					OpSentiElement e = new OpSentiElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setSentiment_value(-1);
					e.setTerm_index(j);
//					sen_list.add(e);
					// 父亲是上层队列的首节点
					int t = 0;
//					editAll(e,pro_list,"3",treeIndex,opTree);
					// 如果和观点对象距离太远，则放弃
					if(pro_list.get(0).getEnd_index()!=-1 && e.getStart_index()-pro_list.get(0).getEnd_index()> maxgap)
						continue;
					while(t<pro_list.size()){
						int parents = pro_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "5", "true");
						opTree.add(node);
					}
					if(pro_flag!=-1){
						pro_list.clear();
					}
					asp_list.clear();
					att_list.clear();
					continue;
				}
				lastTermLevel[0]=lastTermLevel[1];
				lastTermLevel[1]=4;
			}
//		}
		return opTree;
	}

	// 判断是否以异常结束一个观点提取过程
	private static boolean otherEndRule(String seg_i) {
		// TODO Auto-generated method stub
		/* 异常结束，说明没有感情词。
		 * 
		 * ## 要把所有异常结束都放到这个方法里
		 *  # 新 非并列产品（待定）
		 *  # 新 非并列对象(记录,可参考为中评)
		 *  # 新 非并列属性(记录,可参考为中评)
		 *  # 一些特殊标点符号(？！。?!)
		 *  # 间隔过大
		 *  # 达到seg.length
		 * ## 必须清空所有队列
		 * ## continue,重新开始提取新观点
		 */
		if(false){
			return true;
		}
		if(false){
			return true;
		}
		if(false){
			return true;
		}
		if(false){
			return true;
		}
		return false;
	}
	//
	private static void editAll(OpElement e, List<OpElement> list, String string, int treeIndex, List<OpTreeNode> opTree ) {
		int t = 0;
		while(t<list.size()){
			int parents = list.get(t++).getTree_index();
			OpTreeNode node = new OpTreeNode(treeIndex++);
			node.setNodeInfos(e, String.valueOf(parents), "3", null);
			opTree.add(node);
		}
		return;
	}

	//判断是否是比较
	private static boolean isComp() {
		// TODO Auto-generated method stub
		return false;
	}

	//判断是否是并列
	private static boolean isPara(String[] seg, int j, int level) {
		if(j==0)
			return false;
		String[] tempsplit = seg[j - 1].split("/");
		if (tempsplit.length == 2){
			String temp = tempsplit[0].replaceAll("[，。,.^/ ]", ",");
//			return tempsplit[1].equals("c") || tempsplit[1].equals("wn")  || temp.equals(",");
			return tempsplit[1].equals("cc") || tempsplit[1].equals("c") || ((tempsplit[1].equals("wn")  || temp.equals(","))&& (lastTermLevel[0]!=4 || lastTermLevel[1]!=4));
		}else{
//			System.out.println("判断并列失败：上一个分词结果异常");
			return false;
		}
	}

	//
	private static Map<String, Integer> setSaValueMap(String mydict, String category, String savaluepath) {

		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		String[] words = mydict.split("/");
		if (savaluepath.equals("")) {
			int saval = (category.equals("pos")) ? 1 : (-1);
			for (int i = 1; i < words.length; i++)
				resultMap.put(words[i], saval);
		} else {
			// 输入词典里的值！
		}
		return resultMap;
	}
	
	// 评论对象分类
	public static List<Opinion> aspectCategory(List<Opinion> oplist){

		List<Opinion> resultlist = new ArrayList<Opinion>();
		
		for (Opinion op : oplist) {
			float maxd1 = 0;
			Integer category1 = -1;
			float maxd2 = 0;
			Integer category2 = -1;
			if(op.getAttribute().getContent()==null && op.getAspect().getContent()==null){
				for (int i = 0; i < cateDict.size(); i++) {
					float temp = 0;
					temp = W2vUtil.dist(w2v.getWordVector(op.getSentiment().getContent()), w2v.getWordVector(cateDict.get(i)));
					if(temp > similarity){
						boolean b = temp > maxd1;
						maxd1 = b ? temp : maxd1;
						category1 = b ? i : category1;
					}
				}
				op.getAspect().setAspect_category(category1);
				op.getAspect().setAttr_category_centerword(cateDict);
				for (int i = 0; i < pri_cate_dict.size(); i++) {
					float temp = 0;
					temp = W2vUtil.dist(w2v.getWordVector(op.getAspect().getAttr_category_centerword()), w2v.getWordVector(pri_cate_dict.get(i)));
					if(temp > similarity){
						boolean b = temp > maxd2;
						maxd2 = b ? temp : maxd2;
						category2 = b ? i : category2;
					}
				}
				op.getAspect().setAspect_category_centerword(pri_cate_dict,category2);
			}
			for (int i = 0; i < cateDict.size(); i++) {
				float temp = 0;
				if(op.getAttribute().getContent()!=null){
					//如果不空,给属性分类
					temp = W2vUtil.dist(w2v.getWordVector(op.getAttribute().getContent()), w2v.getWordVector(cateDict.get(i)));
				}else{
					//给对象分类
					temp = W2vUtil.dist(w2v.getWordVector(op.getAspect().getContent()), w2v.getWordVector(cateDict.get(i)));
				}
//				boolean b = temp < maxd;
				if(temp > similarity){
					boolean b = temp > maxd1;
					maxd1 = b ? temp : maxd1;
					category1 = b ? i : category1;
				}
			}
			if(category1>-1){
				op.getAspect().setAspect_category(category1);
				op.getAspect().setAspect_category_centerword(pri_cate_dict,pri_category.get(category1));
				op.getAspect().setAttr_category_centerword(cateDict);
			}
			resultlist.add(op);
		}
		return resultlist;
	}

}
