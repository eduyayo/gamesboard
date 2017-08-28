package com.pigdroid.hub.service;

import java.util.List;

import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.persistent.Game;
import com.pigdroid.hub.web.rest.dto.GameProfile;


public interface GameService {

	void handleMessage(HubMessage message);

	GameProfile addGame(HubMessage message);

	List<GameProfile> list(String sessionEmail);

	Game joinGame(String sessionEmail, String id);
	
	void leaveGame(String sessionEmail, String id);
	
}
