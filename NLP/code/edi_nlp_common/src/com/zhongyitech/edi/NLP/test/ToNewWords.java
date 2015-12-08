package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.NewWordsDiscovery;

/*
	args[0]:CRF标注结果文件路径
	args[1]:相似度阈值，默认是0

*/
public class ToNewWords {

	public static void main(String[] args) throws Exception{
		
		float sim = (float) 0.5;
		if(args.length!=2 && args.length!=1){
			System.out.println("请输入两个参数：1.CRF标注结果路径不能为空，2.过滤的相似度阈值");
			return;
		}
		if(args.length==2){
			sim = Float.parseFloat(args[1]);
		}
//		String l2 = CRFsUtil.getNewWords("F:\\share\\result");
		
		NewWordsDiscovery.getNewWords(args[0],sim);
//		System.out.println(l2);
		NewWordsDiscovery.toNewWordsTxt();
		
//		System.out.println("done!");
	}

}
