package com.zhongyitech.edi.hive.udf;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

//用于观点提取的hive transform 自定义类
public class OpUDF extends UDF {

	public ArrayList<ArrayList<String>> evaluate(String commId,String comm, String prodInfo) {
		ArrayList<ArrayList<String>> ps = new ArrayList<ArrayList<String>>();
		try {
			//提取comm文本的观点
			List<Opinion> list = OpMiningUtil.doSa(comm, prodInfo);
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> ls = new ArrayList<String>();
				Opinion o = list.get(i);
				ls.add(commId);
				ls.add(o.get_aspe());
				ls.add(o.get_attr());
				ls.add(o.get_opsa());
				ls.add(o.getOp_start_index() + "," + o.getOp_end_index());
				ps.add(ls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		//测试数据
		ArrayList<String> ls = new ArrayList<String>();
		ls.add(commId);
		ls.add("A1");
		ls.add("B1");
		ls.add("C1");
		ls.add("D1");
		ps.add(ls);
		
		ArrayList<String> ls1 = new ArrayList<String>();
		ls1.add(commId);
		ls1.add("A2");
		ls1.add("B2");
		ls1.add("C2");
		ls1.add("D2");
		ps.add(ls1);
		System.out.println("OpUDF return:"+ps);*/
		
		return ps;
	}

}
