package com.pigdroid.game.model;

import java.util.UUID;

import com.pigdroid.game.model.memento.UndoableObject;

public class Model extends UndoableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id = UUID.randomUUID().getLeastSignificantBits();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
