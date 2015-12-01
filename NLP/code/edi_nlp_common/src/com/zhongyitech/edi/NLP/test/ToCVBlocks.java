package com.zhongyitech.edi.NLP.test;

import java.util.List;

import com.zhongyitech.edi.NLP.util.CreateCVBlocks;
/*
	args[0]:评论文件路径
	args[1]:生成分块路径

*/
public class ToCVBlocks {

	public static void main(String[] args) throws Exception{
		
		if(args.length!=2){
			System.out.println("请输入两个参数：1.评论文件的路径，2.生成分块的路径");
			return;
		}
		
		List<String> l = CreateCVBlocks.getTrainData(args[0]);
		CreateCVBlocks.toCVBlocksTxt(l,args[1]);
	}

}
