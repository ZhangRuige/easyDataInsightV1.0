package com.zhongyitech.edi.hive.udf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.zhongyitech.edi.NLP.util.RivalProductUtil;

//用于提及提取的hive transform 自定义类
public class MentionUDF extends UDF {
		
	public ArrayList<ArrayList<String>> evaluate(String comm,String brand, String model) {
		ArrayList<ArrayList<String>> ps = new ArrayList<ArrayList<String>>();
		try {
			//提取comm文本的观点
			String model_mentions = RivalProductUtil.findRivalProduct(comm);
			
			if(model_mentions!=null && model_mentions.length()>0){
				String []mentions = model_mentions.split(";");
				for (String mention : mentions) {
					ArrayList<String> m = new ArrayList<String>();
					String []fields = mention.split(",");
					if((fields[0].equals(brand) || "null".equals(fields[0].toLowerCase())) 
							&& fields[1].equals(model)){	//排除自己,提到和自己相同的型号，但没提品牌时认为是指自己
						continue;
					}
					
					m.add(fields[0]);
					m.add(fields[1]);
					ps.add(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ps;
	}

}
