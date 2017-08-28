package com.pigdroid.hub.service;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.Message;

public interface MessageServiceInterface extends CRUDInterface<Message> {

	void sendMessage(MessageContext context, HubMessage newMsg, boolean persistOnFail);

	void sendMessage(MessageContext context, HubMessage newMsg);

	void sendErrorMessage(MessageContext context);

	void sendMessageToSession(MessageContext context, HubMessage message);

}
