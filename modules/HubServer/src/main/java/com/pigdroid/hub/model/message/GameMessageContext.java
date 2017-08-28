package com.pigdroid.hub.model.message;

import com.pigdroid.game.controller.GameController;

public class GameMessageContext extends MessageContext {

	private GameController<?> gameController;
	private GameMessage gameMessage;
	private String gameName;

	public GameMessageContext(GameMessage gameMessage, MessageContext context,
			GameController<?> gameController, String gameName) {
		super(context);
		this.setGameController(gameController);
		this.setGameMessage(gameMessage);
		this.gameName = gameName;
	}

	public GameController<?> getGameController() {
		return gameController;
	}

	public void setGameController(GameController<?> gameController) {
		this.gameController = gameController;
	}

	public GameMessage getGameMessage() {
		return gameMessage;
	}

	public void setGameMessage(GameMessage gameMessage) {
		this.gameMessage = gameMessage;
	}

}
