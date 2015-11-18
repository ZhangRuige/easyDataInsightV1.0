package com.zhongyitech.edi.NLP.test;
import java.util.List;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		List<Opinion> oplist = OpMiningUtil.doSa("雨燕同飞 回复 452讷河市杨爽 ：信号差和电池不耐用是三星通病，用了几个三星手机都是这问题，不过三星的屏幕色彩不错", "sss");
		for (int i = 0; i < oplist.size(); i++) {
			System.out.println("op" + i + ":" + "\r\n\t产品：" + oplist.get(i).getProduct().getContent() + "\r\n\t评论对象："
					+ oplist.get(i).getAspect().getContent() + "\r\n\t评论对象属性："
					+ oplist.get(i).getAttribute().getContent() + "\r\n\t评论对象种类："
					+ oplist.get(i).get_aspe() + "\r\n\t情感词："
					+ oplist.get(i).getSentiment().getContent() + "\r\n\t否定词：" + oplist.get(i).getNeg_words()
					+ "\r\n\t情感分类：" + oplist.get(i).getSentiment().getSentiment_category() + "\r\n\t观点句位置：["
					+ oplist.get(i).getOp_start_index() + "," + oplist.get(i).getOp_end_index() + "]" + "\r\n\t观点句："
					+ oplist.get(i).getOp_sent()
					);
		}
	}
}
