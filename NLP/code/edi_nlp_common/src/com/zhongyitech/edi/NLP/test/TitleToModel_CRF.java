package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.CreateCVBlocks;

public class TitleToModel_CRF {

	/*
	 *大小写
	 *空格
	 *不完全匹配? 
	 */
	
	public static void main(String[] args) throws Exception{
		
		if(args.length!=3){
			System.out.println("请输入三个参数：1.标题路径，2.品牌、型号路径，3.生成分块的路径");
			return;
		}
//		long s = System.currentTimeMillis();
		
		String s1 = CreateCVBlocks.markTitle(args[0],args[1]);
		String s2 = CreateCVBlocks.getTrainData_Title(s1,args[2]);
		
		
		System.out.println("done!");
//		long e = System.currentTimeMillis();
		
//		System.out.println(e-s);
	}

}
