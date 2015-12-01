package com.zhongyitech.edi.NLP.model;

public class CRFTag {
	//字
	private String word;
	//词性标签,包括BEMS
	private String pos_tag;
	//特殊标签,包括BIO
	private String spec_tag;
	
	
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPos_tag() {
		return pos_tag;
	}
	public void setPos_tag(String pos_tag) {
		this.pos_tag = pos_tag;
	}
	public String getSpec_tag() {
		return spec_tag;
	}
	public void setSpec_tag(String spec_tag) {
		this.spec_tag = spec_tag;
	}
	
	
}
