package com.sd.absa.customer.productreview;

import java.util.List;

public class Result {
	
	private String link;
	
	private String type;
	
	private String description;
	
	private List<EntitySentiment> entitySentiment;


	public Result(String link, String type, String description, List<EntitySentiment> aspectSentiment) {
		super();
		this.link = link;
		this.type = type;
		this.description = description;
		this.entitySentiment = aspectSentiment;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<EntitySentiment> getAspectSentiment() {
		return entitySentiment;
	}

	public void setAspectSentiment(List<EntitySentiment> aspectSentiment) {
		this.entitySentiment = aspectSentiment;
	}

	@Override
	public String toString() {
		return "Result [link=" + link + ", type=" + type + ", description=" + description + ", entitySentiment=" + entitySentiment + "]";
	}	

}
