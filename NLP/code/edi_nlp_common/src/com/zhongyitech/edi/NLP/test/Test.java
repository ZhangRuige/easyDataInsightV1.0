package com.zhongyitech.edi.NLP.test;

import java.util.List;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		List<Opinion> oplist = OpMiningUtil.doSa("屏好，不知道为什么，4G内存占用比例还是很大的，正常都要60%-70%，那个S助手很麻烦，删又删不掉，长期占用内存，三星自带软件太多了，其他运行速度确实很快", "iPhone");
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
