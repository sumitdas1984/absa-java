package com.sd.absa.productreview;

public class EntitySentiment {

	private String entity;
	
	private String sentiment;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public EntitySentiment(String entity, String sentiment) {
		super();
		this.entity = entity;
		this.sentiment = sentiment;
	}

	@Override
	public String toString() {
		return "EntitySentiment [entity=" + entity + ", sentiment=" + sentiment + "]";
	}
	
}
