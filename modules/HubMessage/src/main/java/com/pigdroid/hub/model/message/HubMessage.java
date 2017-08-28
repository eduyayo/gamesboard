package com.pigdroid.hub.model.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author eduyayo
 *
 */
public class HubMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3444642624849704604L;
	
	public static final String TYPE_ADD_CONTACT		= "ADD_CONTACT";
	public static final String TYPE_ADD_GAME		= "ADD_GAME";
	public static final String TYPE_MSG_CHAT		= "MSG_CHAT";
	public static final String TYPE_MSG_GAME		= "MSG_GAME";
	public static final String TYPE_MSG_DISCONNECT	= "MSG_DISCONNECT";
	
	private String id = UUID.randomUUID().toString();
	private String type;
	private String payload;
	private String from;
	private List<String> to = new ArrayList<String>();
	
	public HubMessage() {
		
	}

	public HubMessage(String type) {
		super();
		this.type = type;
	}

	public HubMessage(String type, String payload) {
		super();
		this.type = type;
		this.payload = payload;
	}

	public HubMessage(String type, String payload, String from,
			String... to) {
		super();
		this.type = type;
		this.payload = payload;
		this.from = from;
		for (String nt : to) {
			this.to.add(nt);
		}
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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}
	
	public HubMessage clearTo() {
		this.to.clear();
		return this;
	}
	
	public void addTo(String recipient) {
		this.to.add(recipient);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	public static class Builder {
		private String type;
		private List<String> to = new ArrayList<String>();
		private String payload;
		private String from;
		
		private Builder() {
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}
		
		public Builder payload(String payload) {
			this.payload = payload;
			return this;
		}
		
		public Builder to(String to) {
			this.to.add(to);
			return this;
		}
		
		public Builder from(String from) {
			this.from = from;
			return this;
		}
		
		public HubMessage build() {
			return new HubMessage(type, payload, from, to.toArray(new String[to.size()]));
		}
	}

}
