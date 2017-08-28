package com.pigdroid.web.socket.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

public class UserSessionPool {
	
	private Object sync = new Object();
	
	private Map<String, UserSession> sessionsBySessionId = 
			new HashMap<String, UserSession>();
	private Map<String, UserSession> sessionsByEmail = 
			new HashMap<String, UserSession>();

	public UserSession getSessionBySessionId(String key) {
		synchronized (sync) {
			return sessionsBySessionId.get(key);
		}
	}

	public UserSession getSessionByEmail(String key) {
		synchronized (sync) {
			return sessionsByEmail.get(key);
		}
	}
	
	public UserSession createSession(AtmosphereResource resource) throws IOException {
		synchronized (sync) {
			String uuid = resource.uuid();
			UserSession session = new UserSession(uuid, resource.getResponse().getWriter(), resource);
			sessionsBySessionId.put(uuid, session);
			return session;
		}
	}
	
	public void removeSessionId(String key) {
		synchronized (sync) {
			UserSession session = sessionsBySessionId.remove(key);
			if (session != null && session.getEmail() != null) {
				sessionsByEmail.remove(session.getEmail());
			}
		}
	}

	public void putSessionByEmail(String email, UserSession session) {
		synchronized (sync) {
			UserSession previousSession = sessionsByEmail.get(email);
			sessionsByEmail.put(email, session);
			if (previousSession != null) {
				previousSession.invalidate();
			}
		}
	}
	
}
