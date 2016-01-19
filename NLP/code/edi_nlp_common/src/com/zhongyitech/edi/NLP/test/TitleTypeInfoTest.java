package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.TitleTypeInfo;

public class TitleTypeInfoTest {
	
	public static void main(String[] args) throws Exception{
		
		String s = TitleTypeInfo.typeExtract("三星 a 31 双卡", "OPPO");
		System.out.println(s);
		
	}
}
