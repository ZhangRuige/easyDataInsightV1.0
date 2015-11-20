package com.zhongyitech.edi.NLP.model;

public class OpElement {
	
	private int tree_index;
	private String content;
	private int start_index;
	private int end_index;
	// 分词列表中的第几个词
	private int term_index;
	
	public OpElement(){
		start_index = -1;
		end_index = -1;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStart_index() {
		return start_index;
	}
	public void setStart_index(int start_index) {
		this.start_index = start_index;
	}
	public int getEnd_index() {
		return end_index;
	}
	public void setEnd_index(int end_index) {
		this.end_index = end_index;
	}
	
//	set
	public void setOpElementInfos(String content, String start_index, String end_index, int tree_index){
		this.content = content;
		if(start_index!=null)
			this.start_index = Integer.parseInt(start_index);
		if(end_index!=null)
			this.end_index = Integer.parseInt(end_index);
		this.tree_index = tree_index;
	}
	public int getTree_index() {
		return tree_index;
	}
	public void setTree_index(int tree_index) {
		this.tree_index = tree_index;
	}
	public int getTerm_index() {
		return term_index;
	}
	public void setTerm_index(int term_index) {
		this.term_index = term_index;
	}
}
