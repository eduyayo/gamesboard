package com.pigdroid.game.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS)
public abstract class Player extends Model {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private PlayerStatusEnum status;
	private String name = null;

	public Player() {

	}

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PlayerStatusEnum status) {
		this.status = status;
	}

}
