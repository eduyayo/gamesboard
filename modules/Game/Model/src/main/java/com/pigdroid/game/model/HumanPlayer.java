package com.pigdroid.game.model;

public class HumanPlayer extends Player {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	
	public HumanPlayer() {
		setStatus(PlayerStatusEnum.INVITED);
	}
	
	public HumanPlayer(String name) {
		super(name);
	}
	
	public HumanPlayer(String name, String email) {
		this(name);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
