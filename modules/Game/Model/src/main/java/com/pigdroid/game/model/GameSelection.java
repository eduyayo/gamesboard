package com.pigdroid.game.model;

public class GameSelection extends Model {

	private static final long serialVersionUID = 1L;

	private String playerName;
	
	private Long modelId;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	@Override
	public String toString() {
		return "GameSelection [playerName=" + playerName + ", modelId="
				+ modelId + "]";
	}

}
