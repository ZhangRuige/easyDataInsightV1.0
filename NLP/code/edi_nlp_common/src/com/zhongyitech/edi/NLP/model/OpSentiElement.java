package omsaTest.model;

public class OpSentiElement extends OpElement {
	
	private String sentiment_category;
	private int sentiment_value;
	
	public String getSentiment_category() {
		return sentiment_category;
	}

	public void setSentiment_category(String sentiment_category) {
		this.sentiment_category = sentiment_category;
	}

	public int getSentiment_value() {
		return sentiment_value;
	}

	public void setSentiment_value(int sentiment_value) {
		this.sentiment_value = sentiment_value;
	}

}
