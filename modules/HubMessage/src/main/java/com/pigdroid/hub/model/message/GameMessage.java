package com.pigdroid.hub.model.message;

import java.io.Serializable;

public class GameMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_JOIN = "JOIN";
	public static final String TYPE_REJECT = "REJECT";
	public static final String TYPE_MSG = "MSG";

	private String id = Long.toString(System.nanoTime());
	private String type;
	private String modelId;
	private String payload;

	public GameMessage() {
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private String type;
		private String modelId;
		private String payload;

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder modelId(String modelId) {
			this.modelId = modelId;
			return this;
		}

		public Builder payload(String payload) {
			this.payload = payload;
			return this;
		}
		
		public GameMessage build() {
			GameMessage ret = new GameMessage();
			ret.setType(this.type);
			ret.setModelId(this.modelId);
			ret.setPayload(this.payload);
			return ret;
		}
	}

}