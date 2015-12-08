package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.RivalProductUtil;

public class FindRivalProductTest {

	public static void main(String[] args) throws Exception {
		
		String path = "corpus/part-r-00000_2.5w_comms";
		String str = RivalProductUtil.findRivalProduct(IoUtil.readTxt(path));
		
		System.out.println("result:");
		System.out.println(str);
	}

}
