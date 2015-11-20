package com.zhongyitech.edi.NLP.model;

import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class Comment {
	
	private String comm_id;
	private String comm_content;
//	private String[] comm_segment;//有必要吗？
	private String[] comm_opnion;
	private String[] comm_aspect;//aspect对应的词典应该分开
	private Integer comm_sa;
	
	private String comm_product;
	private String comm_provider;
	private String comm_time;
	
	
	
	public String getComm_id() {
		return comm_id;
	}
	public void setComm_id(String comm_id) {
		this.comm_id = comm_id;
	}
	public String getComm_content() {
		return comm_content;
	}
	public void setComm_content(String comm_content) {
		this.comm_content = comm_content;
	}
	public String[] getComm_opnion() {
		return comm_opnion;
	}
	public void setComm_opnion(String[] comm_opnion) {
		this.comm_opnion = comm_opnion;
	}
//	public String[] getComm_segment() {
//		return comm_segment;
//	}
//	public void setComm_segment(String[] comm_segment) {
//		this.comm_segment = comm_segment;
//	}
	public String[] getComm_aspect() {
		return comm_aspect;
	}
	public void setComm_aspect(String[] comm_aspect) {
		this.comm_aspect = comm_aspect;
	}
	public Integer getComm_sa() {
		return comm_sa;
	}
	public void setComm_sa(Integer comm_sa) {
		this.comm_sa = comm_sa;
	}
	public String getComm_product() {
		return comm_product;
	}
	public void setComm_product(String comm_product) {
		this.comm_product = comm_product;
	}
	public String getComm_provider() {
		return comm_provider;
	}
	public void setComm_provider(String comm_provider) {
		this.comm_provider = comm_provider;
	}
	public String getComm_time() {
		return comm_time;
	}
	public void setComm_time(String comm_time) {
		this.comm_time = comm_time;
	}
	
	
	public void commSentimentAnalyse(String dict1,String dict2, String dict3, int n){
		
		int pos_count=0;
		int neg_count=0;
		int not_count=0;
		
		String op = this.getComm_opnion()[n];
		List<Term> list = ToAnalysis.parse(op);
		for(int i=0;i<list.size();i++){
			
			if(dict1.contains(list.get(i).toString().replaceAll("/[a-z]", "")))
				pos_count++;
			if(dict2.contains(list.get(i).toString().replaceAll("/[a-z]", "")))
				neg_count++;
			if(dict3.contains(list.get(i).toString().replaceAll("/[a-z]", "")))
				not_count++;
			
		}
		
		int temp = pos_count-neg_count;
		if(temp==0)
			this.setComm_sa(0);
		else{
			if(1==not_count%2){
				this.setComm_sa((temp)*(-1) > 0?1:(-1));
			}else{
				this.setComm_sa( temp > 0?1:(-1));
			}
		}
	}

}
