package omsaTest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import com.ansj.vec.Word2VEC;

import omsaTest.model.OpAspElement;
import omsaTest.model.OpElement;
import omsaTest.model.OpSentiElement;
import omsaTest.model.OpTreeNode;
import omsaTest.model.Opinion;

public class OpMiningUtil {
	/*
	 * dicts... 0品牌词 1对象词 2属性词 3正面词 4负面词 5否定词
	 */
	private static String[] dicts = {"dicts/dict0.txt","dicts/dict1.txt","dicts/dict2.txt","dicts/dict3.txt","dicts/dict4.txt","dicts/dict5.txt"};
	private static String category = "dicts/categoryDicts.txt";
	private static String corpus = "corpus/w2vTrainSet.txt";
	private static String stpwdict = "dicts/stopWordDict.txt";
	//用于标注前两个词在观点树中的层次
	private static int[] lastTermLevel = {0,0};
	
	//对象分类相似度阈值
	private static float similarity = (float) 0.35 ;
	
	public static void trainModel() throws Exception{
		W2vUtil.word2vecModelTrain(corpus);
	}
	public static void trainModel(String file) throws Exception{
		W2vUtil.word2vecModelTrain(file);
	}
	
	public static List<Opinion> doSa(String words,String product) throws Exception{
		String words1 = preTreatWords(words);
		
		List<String> cateDict = DictMakeUtil.makeCateDict(category);//观点分类词典读取
		String[] dict = DictMakeUtil.makeOpDict(dicts);//观点元素词典
		DictMakeUtil.modifyDict(dict,"/");//分词词典
		DictMakeUtil.modifyDict(cateDict);//分词词典
		
		List<Term> list1 = ToAnalysis.parse(words1);
		
		setStopWord(stpwdict);
		List<Term> list = FilterModifWord.modifResult(list1);//去停用词
		List<Opinion> oplist = OpMiningUtil.opMining(words, dict ,list, product);//观点提取
		oplist = OpMiningUtil.aspectCategory(oplist,cateDict);//观点对象分类
		return oplist;
	}
	
	private static void setStopWord(String stw) throws Exception {

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
	
	// 去停用词
	
	
	// 观点提取
	public static List<Opinion> opMining(String w, String[] dict, List<Term> list, String p){
		
		List<OpTreeNode> opTree = opTreeConstruct(w,dict,list,p);
		return sentimentAnalysis(dict,opExtract(w,list,dict,opTree));
		
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
	private static List<Opinion> opExtract(String w, List<Term> list, String[] dict, List<OpTreeNode> opTree) {
		
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
			if (dict[5].contains("/" + segsplit[0] + "/")) {
				
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
		}
		return op;
	}
	
	// 观点树构造,按照规则.
	public static List<OpTreeNode> opTreeConstruct(String w, String[] dict, List<Term> list, String p) {

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			s.append(list.get(i).toString() + " ");
		}

		List<OpTreeNode> opTree = new ArrayList<OpTreeNode>();
		opTree.add(new OpTreeNode());
		int treeIndex = 1;
		int pro_flag = 0;
		int fromIndex = 0;
		// 产品队列
		List<OpElement> pro_list = new ArrayList<>();
		// 对象队列
		List<OpElement> asp_list = new ArrayList<>();
		// 属性队列
		List<OpElement> att_list = new ArrayList<>();
//		// 情感词队列
//		List<OpElement> sen_list = new ArrayList<>();
		
		String product = p;
		// 外部有指定的产品品牌型号
		if (!product.equals("")&&product!=null) {
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
				// 是对象
				else if (dict[1].contains("/" + segsplit[0] + "/")) {
					// 不是并列不是比较（就是新的对象）
					if(!isPara(seg, j, 2)&&!isComp()){
						// 清空列表
						asp_list.clear();
					}
					if(isComp()){
						continue;
					}
					OpElement e = new OpAspElement();
					int temp = w.indexOf(segsplit[0], fromIndex);
					fromIndex = temp - 1 + segsplit[0].length();
					e.setOpElementInfos(segsplit[0], String.valueOf(temp),
							String.valueOf(fromIndex), treeIndex);
					e.setTerm_index(j);
					asp_list.add(e);
					// 清空所有更低级的队列
					att_list.clear();
					if(pro_list.size()==0)
						continue;
					int t = 0;
					// 对上层队列的所有元素新增孩子节点
					while(t<pro_list.size()){
						// 父亲是上层队列的首节点
						int parents = pro_list.get(t++).getTree_index();
						OpTreeNode node = new OpTreeNode(treeIndex++);
						node.setNodeInfos(e, String.valueOf(parents), "2", null);
						opTree.add(node);
					}
					
					lastTermLevel[0]=lastTermLevel[1];
					lastTermLevel[1]=2;
					continue;
				}
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
					if(asp_list.size()==0){
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
						while(t<asp_list.size()){
							int parents = asp_list.get(t++).getTree_index();
							OpTreeNode node = new OpTreeNode(treeIndex++);
							node.setNodeInfos(e, String.valueOf(parents), "4", "true");
							opTree.add(node);
						}
						asp_list.clear();
						continue;
					}
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
						while(t<asp_list.size()){
							int parents = asp_list.get(t++).getTree_index();
							OpTreeNode node = new OpTreeNode(treeIndex++);
							node.setNodeInfos(e, String.valueOf(parents), "4", "true");
							opTree.add(node);
						}
						asp_list.clear();
						continue;
					}
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
				lastTermLevel[0]=lastTermLevel[1];
				lastTermLevel[1]=4;
			}
//		}
		return opTree;
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
	public static List<Opinion> aspectCategory(List<Opinion> oplist, List<String> cateDict)
			throws Exception {

		Word2VEC w2v = new Word2VEC();
	    w2v.loadJavaModel("model/vector.mod");
		
		List<Opinion> resultlist = new ArrayList<Opinion>();
		for (Opinion op : oplist) {
//			float maxd = 999999;
//			Integer category = 999999;
			float maxd = 0;
			Integer category = -1;
			for (int i = 0; i < cateDict.size(); i++) {
				float temp = 0;
				if(op.getAspect().getContent()==null){
					//如果空,给属性分类
					temp = W2vUtil.dist(w2v.getWordVector(op.getAttribute().getContent()), w2v.getWordVector(cateDict.get(i)));
				}else{
					//给对象分类
					temp = W2vUtil.dist(w2v.getWordVector(op.getAspect().getContent()), w2v.getWordVector(cateDict.get(i)));
				}
//				boolean b = temp < maxd;
				if(temp > similarity){
					boolean b = temp > maxd;
					maxd = b ? temp : maxd;
					category = b ? i : category;
				}
			}
			op.getAspect().setAspect_category(category);
			op.getAspect().setAspect_category_centerword(cateDict);
			resultlist.add(op);
		}
		return resultlist;
	}

}
