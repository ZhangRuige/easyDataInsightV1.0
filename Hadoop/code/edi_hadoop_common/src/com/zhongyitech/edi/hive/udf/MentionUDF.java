package com.zhongyitech.edi.hive.udf;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.zhongyitech.edi.NLP.util.RivalProductUtil;

//用于提及提取的hive transform 自定义类
public class MentionUDF extends UDF {
		
	public List<List<String>> evaluate(String cid,String comm/*,String brand, String model*/) {
		List<List<String>> model_mentions = new ArrayList<List<String>>();
		try {
			//提取comm文本的观点
			model_mentions = RivalProductUtil.findRivalProduct(comm);
			
			if( model_mentions.size() > 0 ){
				for (List<String> mention : model_mentions) {
					mention.add(0, cid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model_mentions;
	}

}
