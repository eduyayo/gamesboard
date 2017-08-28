package com.pigdroid.hub.service;

import com.pigdroid.hub.model.message.HubMessage;

public interface MessageService  {

	void sendMessage(HubMessage newMsg);

	String handleMessage(HubMessage message);

}
