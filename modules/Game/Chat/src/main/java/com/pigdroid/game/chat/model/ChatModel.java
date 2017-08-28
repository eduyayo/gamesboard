package com.pigdroid.game.chat.model;

import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.model.GameModel;

public class ChatModel extends GameModel {
	
	public static final int MAX_MESSAGES = 30;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long messageCounter = 0L;
	
	private Map<Integer, ChatSelection> messages = new TreeMap<Integer, ChatSelection>();

	public Map<Integer, ChatSelection> getMessages() {
		return messages;
	}

	public void setMessages(Map<Integer, ChatSelection> messages) {
		this.messages = messages;
	}

	public Long getMessageCounter() {
		return messageCounter;
	}

	public void setMessageCounter(Long messageCounter) {
		this.messageCounter = messageCounter;
	}
	

}
