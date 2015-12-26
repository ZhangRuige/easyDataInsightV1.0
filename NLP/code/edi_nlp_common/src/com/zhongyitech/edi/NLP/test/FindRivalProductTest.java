package com.zhongyitech.edi.NLP.test;

import java.util.List;

import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.RivalProductUtil;

public class FindRivalProductTest {

	public static void main(String[] args) throws Exception {
		
//		String path = "corpus/part-r-00000_2.5w_comms";
		List list = RivalProductUtil.findRivalProduct("啊啊啊啊啊啊");
		
		System.out.println("result:");
		System.out.println(list);
		
		System.out.println(list.size());
	}

}
