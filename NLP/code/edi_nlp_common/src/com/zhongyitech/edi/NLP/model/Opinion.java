package com.zhongyitech.edi.NLP.model;

import java.util.List;

public class Opinion {

	private OpElement product;
	private OpAspElement aspect;
	private OpElement attribute;
	private OpSentiElement sentiment;
	private List<String> neg_words;

	private String raw_data;

	private static int maxleft = 3;
	private static int maxright = 1;
	
	public OpElement getProduct() {
		return product;
	}

	public void setProduct(OpElement product) {
		this.product = product;
	}

	public OpAspElement getAspect() {
		return aspect;
	}

	public void setAspect(OpAspElement aspect) {
		this.aspect = aspect;
	}

	public OpElement getAttribute() {
		return attribute;
	}

	public void setAttribute(OpElement attribute) {
		this.attribute = attribute;
	}

	public OpSentiElement getSentiment() {
		return sentiment;
	}

	public void setSentiment(OpSentiElement sentiment) {
		this.sentiment = sentiment;
	}

	public String getRaw_data() {
		return raw_data;
	}

	public void setRaw_data(String raw_data) {
		this.raw_data = raw_data;
	}

	// 获得上下文
	public String getOp_sent() {
		if (product.getStart_index() == -1) {
			if(aspect.getStart_index() == -1){
				if(attribute.getStart_index() == -1){
					return raw_data.substring(sentiment.getStart_index(), sentiment.getEnd_index() + 1);
				}
				return raw_data.substring(attribute.getStart_index(), sentiment.getEnd_index() + 1);
			}else{
				return raw_data.substring(aspect.getStart_index(), sentiment.getEnd_index() + 1);
			}
		} else {
			return raw_data.substring(product.getStart_index(), sentiment.getEnd_index() + 1);
		}
	}

	public int getOp_start_index() {
		if (product.getStart_index() == -1) {
			if(aspect.getStart_index() == -1){
				if(attribute.getStart_index() == -1){
					return sentiment.getStart_index();
				}
				return attribute.getStart_index();
			}else{
				return aspect.getStart_index();
			}
		} else {
			return product.getStart_index();
		}
	}

	public int getOp_end_index() {
		return sentiment.getEnd_index();
	}

	public int getTerm_start_index() {
		if (product.getStart_index() == -1) {
			if(aspect.getStart_index() == -1){
				if(attribute.getStart_index() == -1){
//					return sentiment.getTerm_index()-maxleft;
					return 0;
				}
				return attribute.getTerm_index()-maxleft;
			}else{
				return aspect.getTerm_index()-maxleft;
			}
		} else {
			return product.getTerm_index()-maxleft;
		}
	}

	public int getTerm_end_index() {
		return sentiment.getTerm_index()+maxright;
	}

	// 获得情感值
	public String getOp_sa() {
		return sentiment.getSentiment_category();
	}

	public List<String> getNeg_words() {
		return neg_words;
	}

	public void setNeg_words(List<String> neg_words) {
		this.neg_words = neg_words;
	}

	//
	public String get_prod() {
		return product.getContent();
	}
	//一级分类还是一级分类
	public String get_aspe() {
		return aspect.getAspect_category_centerword();
//		return aspect.getContent();
	}
	//二级分类是总分类
	public String get_attr() {
//		return attribute.getContent();
		return aspect.getAttr_category_centerword();
	}

	public String get_sent() {
		return sentiment.getContent();
	}

	public String get_opsa() {
		return String.valueOf(sentiment.getSentiment_value());
	}
}
