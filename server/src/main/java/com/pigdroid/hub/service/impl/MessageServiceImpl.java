package com.pigdroid.hub.service.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jboss.aerogear.unifiedpush.SenderClient;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.service.GameService;
import com.pigdroid.hub.service.MessageService;
import com.pigdroid.social.service.UserService;

@Service
public class MessageServiceImpl implements MessageService {
	
	private static final int TIMEOUT_SEC = 30;

	@Autowired
	private UserService userService;
	
	@Autowired
	private GameService gameService;


	private SenderClient pushSender = SenderClient.withRootServerURL("https://aerogear-pigdroidservices.rhcloud.com/ag-push/").build();
//			.pushApplicationId("51b51d7d-cba0-4af1-a420-d9b4d6dae877")
//			.masterSecret("af80e72d-e9f0-4e83-b543-80f6662e8f7a").build();
	
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	@Transactional
	public void sendMessage(HubMessage newMsg) {
		if (newMsg.getTo() == null || newMsg.getTo().size() == 0) {
			throw new RuntimeException("Message has an empty TO field.");
		}
		
		try {
			 pushSender.send(new UnifiedMessage.Builder()
             .pushApplicationId("51b51d7d-cba0-4af1-a420-d9b4d6dae877")
             .masterSecret("af80e72d-e9f0-4e83-b543-80f6662e8f7a")
             .aliases(userService.getDeviceTokens(newMsg.getTo()))
             .sound("default") 
             .attribute("payload", new ObjectMapper().writeValueAsString(newMsg))
             .build());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Transactional
	public boolean sendMessageOrFalse(final HubMessage newMsg) {
		boolean ret;
		if (newMsg.getTo() == null || newMsg.getTo().size() == 0) {
			throw new RuntimeException("Message has an empty TO field.");
		}
		
		final CountDownLatch latch = new CountDownLatch(1);
		Thread task = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					pushSender.send(
							new UnifiedMessage.Builder()
			             .pushApplicationId("51b51d7d-cba0-4af1-a420-d9b4d6dae877")
			             .masterSecret("af80e72d-e9f0-4e83-b543-80f6662e8f7a")
			             .aliases(userService.getDeviceTokens(newMsg.getTo()))
			             .sound("default") 
			             .attribute("payload", new ObjectMapper().writeValueAsString(newMsg))
			             .build());
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					latch.countDown();
				}
			}
		});
		task.start();
		try {
			ret = latch.await(TIMEOUT_SEC, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			ret = false;
		}
 		return ret;
	}

	@Override
	public String handleMessage(HubMessage message) {
		String ret = "ERROR";
		switch (message.getType()) {
		case HubMessage.TYPE_ADD_CONTACT:
			ret = "ERROR: Cannot send an TYPE_ADD_CONTACT message.";
			break;
		case HubMessage.TYPE_ADD_GAME:
			ret = "ERROR: Cannot send an TYPE_ADD_GAME message.";
			break;
		case HubMessage.TYPE_MSG_CHAT:
			if (sendMessageOrFalse(message)) {
				ret = "OK";
			} else {
				ret = "ERROR";
			}
			break;
		case HubMessage.TYPE_MSG_GAME:
			gameService.handleMessage(message);
			break;
		default:
			break;
		}
		return ret;
	}
	
}
