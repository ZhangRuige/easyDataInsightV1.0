package com.zhongyitech.edi.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.zhongyitech.edi.NLP.util.TitleTypeInfo;

//用于从商品title中提取指定品牌的型号名称
public class Title2ModelUDF extends UDF {
	public String evaluate(String title, String brand) {
		return TitleTypeInfo.typeExtract( title, brand);
	}
}
