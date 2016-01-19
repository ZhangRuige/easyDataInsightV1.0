package com.zhongyitech.edi.NLP.test;

import java.util.ArrayList;
import java.util.List;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

public class OmSaTest2 {

	public static void main(String[] args) throws Exception {
		
		String st = IoUtil.readTxt("corpus/comms_14w_20151209");
		String[] c = st.split("\n");
		
		String product = "iphone";
		
		List<Opinion> list = new ArrayList<Opinion>();
		long s = System.currentTimeMillis();
		for(String str :c){
			List<Opinion> templ = OpMiningUtil.doSa(str, product);
			list.addAll(templ);
		}
		long e = System.currentTimeMillis();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("op" + i + ":" + "\r\n\t产品：" + list.get(i).getProduct().getContent()
					+ "\r\n\t评论对象一级分类词：" + list.get(i).get_aspe()
					+ "\r\n\t评论对象start：" + list.get(i).getAspect().getStart_index()
					+ "\r\n\t评论对象end：" + list.get(i).getAspect().getEnd_index()
					+ "\r\n\t评论对象二级分类词：" + list.get(i).getAttribute().getContent()
					+ "\r\n\t评论对象种类：" + list.get(i).get_attr()
					+ "\r\n\t情感词：" + list.get(i).getSentiment().getContent()
					+ "\r\n\t否定词：" + list.get(i).getNeg_words()
					+ "\r\n\t情感分类：" + list.get(i).getSentiment().getSentiment_category()
					+ "\r\n\t观点句位置：[" + list.get(i).getOp_start_index() + "," + list.get(i).getOp_end_index() + "]"
					+ "\r\n\t观点句：" + list.get(i).getOp_sent()
					);
		}
		System.out.println(e-s);
	}

}
