package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.W2vUtil;

public class W2vTest {

	public static void main(String[] args) throws Exception {
		System.out.println("now w2v training...");
		W2vUtil.word2vecModelTrain("");
		System.out.println("done!");
	}

}
