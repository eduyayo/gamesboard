package com.pigdroid.hub.web.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.service.GameServiceInterface;
import com.pigdroid.hub.service.UserServiceInterface;

public class MessageHandler {
	private static final Log LOG = LogFactory.getLog(MessageHandler.class);

	UserServiceInterface userService = null;
	GameServiceInterface gameService = null;
	
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}
	
	public void setGameService(GameServiceInterface gameService) {
		this.gameService = gameService;
	}

	public void handleMessage(MessageContext context) {
		HubMessage msg = context.getMessage();
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("handleMessage() starts with message %s.", msg == null ? null : msg.getType()));
		}
		if (msg != null) {
			switch (msg.getType()) {
			case HubMessage.TYPE_SEND_PASS:
				userService.doSendPassword(context);
				break;
			case HubMessage.TYPE_REGISTER:
				userService.doRegister(context);
				break;
			case HubMessage.TYPE_LOGIN:
				userService.doLogin(context);
				break;
			case HubMessage.TYPE_LOGOUT:
				break;
			case HubMessage.TYPE_ADD_CONTACT:
				userService.doAddContact(context);
				break;
			case HubMessage.TYPE_REMOVE_CONTACT:
				userService.doRemoveContact(context);
				break;
			case HubMessage.TYPE_REQUEST_ROOSTER:
				userService.doRetrieveRooster(context);
				break;
			case HubMessage.TYPE_USER_LIST:
				userService.doListUsers(context);
				break;
			case HubMessage.TYPE_MSG:
				gameService.handleMessage(context);
				break;
			default:
				break;
			}
		}
	}

}
