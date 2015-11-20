package omsaTest.model;

import java.util.List;

public class Opinion11091005 {
	
	private String product;
	private String aspect;
	private Integer aspect_category;
	
	private String attribute;
	
	private List<String> sentiment_words;
	private String sentiment_category;
	
	private Integer op_start;
	private Integer op_end;
	
	private Integer aspect_start;
	private Integer aspect_end;
	
	private List<CRFTag> words;
	private String op_sent;
//	否定词
	private List<String> neg_words;
	
	public String getAspect() {
		return aspect;
	}
	public void setAspect(String aspect) {
		this.aspect = aspect;
	}
	public List<String> getSentiment_words() {
		return sentiment_words;
	}
	public void setSentiment_words(List<String> sentiment_words) {
		this.sentiment_words = sentiment_words;
	}
	public String getSentiment_category() {
		return sentiment_category;
	}
	public void setSentiment_category(String sentiment_category) {
		this.sentiment_category = sentiment_category;
	}
	public Integer getOp_start() {
		return op_start;
	}
	public void setOp_start(Integer op_start) {
		this.op_start = op_start;
	}
	public Integer getOp_end() {
		return op_end;
	}
	public void setOp_end(Integer op_end) {
		this.op_end = op_end;
	}
	public List<CRFTag> getWords() {
		return words;
	}
	public void setWords(List<CRFTag> words) {
		this.words = words;
	}
	public String getOp_sent() {
		return op_sent;
	}
	public void setOp_sent(String op_sent) {
		this.op_sent = op_sent;
	}
	public Integer getAspect_start() {
		return aspect_start;
	}
	public void setAspect_start(Integer aspect_start) {
		this.aspect_start = aspect_start;
	}
	public Integer getAspect_end() {
		return aspect_end;
	}
	public void setAspect_end(Integer aspect_end) {
		this.aspect_end = aspect_end;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public List<String> getNeg_words() {
		return neg_words;
	}
	public void setNeg_words(List<String> neg_words) {
		this.neg_words = neg_words;
	}
	public Integer getAspect_category() {
		return aspect_category;
	}
	public void setAspect_category(Integer aspect_category) {
		this.aspect_category = aspect_category;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
