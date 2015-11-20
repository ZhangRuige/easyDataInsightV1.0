package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.model.Comment;

public class SaTest {

	public static void main(String[] args) {
		
		Comment com = new Comment();
		String[] com_op = new String[1];
		
		String comment = "手机不好";
		String pos_dict = "好";
		String neg_dict = "不好";
		String not_dict = "不";
		com.setComm_content(comment);
		com_op[0] = comment;
		com.setComm_opnion(com_op);
		
		com.commSentimentAnalyse(pos_dict, neg_dict, not_dict, 0);
		
		System.out.println(com.getComm_sa());
		
	}

}
