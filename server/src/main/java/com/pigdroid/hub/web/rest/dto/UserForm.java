package com.pigdroid.hub.web.rest.dto;

import java.io.Serializable;

public class UserForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6592962240129477026L;

	private String deviceToken;
	private String login; //should be email

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return login;
	}

	public void setEmail(String login) {
		this.login = login;
	}

}
