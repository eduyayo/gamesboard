package com.pigdroid.game.chat.model;

import java.util.Comparator;

import com.pigdroid.game.model.GameSelection;

public class ChatSelection extends GameSelection {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9092468365526250933L;

	private String text;
	
	private static Comparator<ChatSelection> comparator = new Comparator<ChatSelection>() {

		public int compare(ChatSelection o1, ChatSelection o2) {
			return o2.getId().compareTo(o1.getId());
		}
		
	};
	
	public static Comparator<ChatSelection> getComparator() {
		return comparator; 
	}

	public ChatSelection() {
		
	}

	public ChatSelection(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
