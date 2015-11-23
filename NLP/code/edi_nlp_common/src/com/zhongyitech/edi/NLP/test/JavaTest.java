package com.zhongyitech.edi.NLP.test;

import com.zhongyitech.edi.NLP.util.CRFsUtil;

public class JavaTest {

	public static void main(String[] args) throws Exception {
//		List<String> l = CRFsUtil.getTrainData("屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n屏幕好手感好\t0,1;3,4\r\n");
//		System.out.println(l);
		
		String l = CRFsUtil.getNewWords("屏\tBn\tB\r\n幕\tEn\tI\r\n大\tSa\tO\r\n啊\tBn\tB\r\n啊\tEn\tI\r\n大\tSa\tO\r\n屏\tBn\tB\r\n幕\tEn\tI\r\n大\tSa\tO\r\n啊\tBn\tB\r\n啊\tEn\tI\r\n大\tSa\tO\r\n");
		System.out.println(l);
	}
	
	private static void test(){
		
	}
	
}
