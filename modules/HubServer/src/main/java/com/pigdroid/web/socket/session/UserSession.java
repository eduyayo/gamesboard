package com.pigdroid.web.socket.session;

import java.io.PrintWriter;

import org.atmosphere.cpr.AtmosphereResource;

public class UserSession {
	
	private String id;
	private String email;
	private PrintWriter writer;
	private AtmosphereResource resource;

	public UserSession(String uuid, PrintWriter writer, AtmosphereResource resource) {
		this.id = uuid;
		this.writer = writer;
		this.resource = resource;
	}
	
	public PrintWriter getWriter() {
		return writer;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void invalidate() {
		resource.resume();
	}

}
