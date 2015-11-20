package omsaTest.model;

import java.util.List;

public class OpAspElement extends OpElement {
	
	private Integer aspect_category;
	private String aspect_category_centerword;

	public Integer getAspect_category() {
		return aspect_category;
	}

	public void setAspect_category(Integer aspect_category) {
		this.aspect_category = aspect_category;
	}
	
	public void setAspect_category_centerword(List<String> cateDict) {
		if(aspect_category!=-1)
			this.aspect_category_centerword = cateDict.get(this.aspect_category);
		else
			this.aspect_category_centerword = "其他";
	}

	public String getAspect_category_centerword() {
		return aspect_category_centerword;
	}

	public void setAspect_category_centerword(String aspect_category_centerword) {
		this.aspect_category_centerword = aspect_category_centerword;
	}
}
