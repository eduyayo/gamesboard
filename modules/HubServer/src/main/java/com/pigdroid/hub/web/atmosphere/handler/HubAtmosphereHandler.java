package com.pigdroid.hub.web.atmosphere.handler;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;

import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.util.spring.ContextProvider;
import com.pigdroid.hub.web.handler.MessageHandler;
import com.pigdroid.web.socket.session.UserSession;
import com.pigdroid.web.socket.session.UserSessionPool;

import flexjson.JSONDeserializer;

public class HubAtmosphereHandler extends AtmosphereHandlerAdapter {
	
	private static final Log LOG = LogFactory.getLog(HubAtmosphereHandler.class);
	
	private static UserSessionPool sessionsPool = new UserSessionPool();

	protected MessageHandler messageHandler = null;
	
	protected MessageHandler getMessageHandler() {
		if (messageHandler == null) {
			messageHandler = (MessageHandler) ContextProvider.getApplicationContext().getBean("messageHandler");
		}
		return messageHandler;
	}
	
	@Override
	public void onRequest(AtmosphereResource resource) throws IOException {
//		if (LOG.isTraceEnabled()) {
//			LOG.trace(String.format("onRequest() starts... for resource %s.", resource.uuid()));
//		}
		AtmosphereRequest request = resource.getRequest();

		String method = request.getMethod();
		if ("get".equalsIgnoreCase(method)) {
//			if (LOG.isTraceEnabled()) {
//				LOG.trace("Suspending request after GET call.");
//			}
			UserSession session = null;//sessionsPool.getSessionBySessionId(resource.uuid());
			if (session == null) {
				session = sessionsPool.createSession(resource);
				resource.suspend();
				resource.getResponse().setContentType("application/json");
				getMessageHandler().handleMessage(new MessageContext(sessionsPool, session, null, resource.getRequest().getLocale()));
			}
		} else if ("post".equalsIgnoreCase(method)) {
//			if (LOG.isTraceEnabled()) {
//				LOG.trace("Processing POST message.");
//			}
			HubMessage msg = parseMessage(request);
			if (msg == null) {
				return;
			}
			getMessageHandler().handleMessage(new MessageContext(
					sessionsPool, sessionsPool.getSessionBySessionId(resource.uuid()), msg, resource.getRequest().getLocale()));
		}
 	}

	private HubMessage parseMessage(AtmosphereRequest request)
			throws IOException {
		JSONDeserializer<HubMessage> deserializer = new JSONDeserializer<HubMessage>();
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(request.getReader());
		String line = reader.readLine();
		while (line != null) {
			builder.append(line);
			line = reader.readLine();
		}
		String stringMessage = builder.toString().trim();
		HubMessage msg = null;
		if (!"".equals(stringMessage)) {
			msg = deserializer.deserialize(stringMessage);
		}
		return msg;
	}

	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		HubMessage message = parseMessage(event);
		if (message == null) {
			return;
		}

		AtmosphereResource resource = event.getResource();
		AtmosphereResponse response = resource.getResponse();

		if (resource.isSuspended()) {
			getMessageHandler().handleMessage(
					new MessageContext(sessionsPool, sessionsPool.getSessionBySessionId(resource.uuid()), message, resource.getRequest().getLocale()));
			switch (resource.transport()) {
			case JSONP:
			case LONG_POLLING:
				event.getResource().resume();
				break;
			case WEBSOCKET:
			case STREAMING:
				response.getWriter().flush();
				break;
			default:
				break;
			}
		} else if (!event.isResuming()) {
			getMessageHandler().handleMessage(
					new MessageContext(sessionsPool, sessionsPool.getSessionBySessionId(resource.uuid()), new HubMessage(HubMessage.TYPE_LOGOUT), resource.getRequest().getLocale()));
			// event.broadcaster().broadcast(
			// new Data("Someone", "say bye bye!").toString());
		}

	}

	private HubMessage parseMessage(AtmosphereResourceEvent event) {
		JSONDeserializer<HubMessage> deserializer = new JSONDeserializer<HubMessage>();
		String msg = event.getMessage().toString().trim();
		HubMessage message = null;
		if (!"".equals(msg)) {
			message = deserializer.deserialize(msg);
		}
		return message;
	}

}
