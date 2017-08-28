package com.pigdroid.hub.service;

import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.Game;

public interface GameServiceInterface extends CRUDInterface<Game> {
	
	public void handleMessage(MessageContext context);
}
