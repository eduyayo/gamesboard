package com.pigdroid.hub.model.message;

import java.io.PrintWriter;
import java.util.Locale;

import com.pigdroid.web.socket.session.UserSession;
import com.pigdroid.web.socket.session.UserSessionPool;

public class MessageContext {

	private HubMessage message;
	private UserSessionPool sessionPool;
	private UserSession session;
	private Locale locale;
	
	protected MessageContext(MessageContext src) {
		this.message = src.message;
		this.sessionPool = src.sessionPool;
		this.session = src.session;
		this.locale = locale;
	}

	public MessageContext(UserSessionPool sessionPool, UserSession session, HubMessage message, Locale locale) {
		this.sessionPool = sessionPool;
		this.session = session;
		this.message = message;
	}

	public HubMessage getMessage() {
		return message;
	}

	public void setUserEmail(String email) {
		session.setEmail(email);
		sessionPool.putSessionByEmail(email, session);
	}
	
	public String getUserEmail() {
		return session.getEmail();
	}
	
	public PrintWriter getWriter() {
		return session.getWriter();
	}
	
	public PrintWriter getWriter(String userEmail) {
		UserSession sess = sessionPool.getSessionByEmail(userEmail);
		if (sess != null) {
			return sess.getWriter();
		}
		return null;
	}

}
