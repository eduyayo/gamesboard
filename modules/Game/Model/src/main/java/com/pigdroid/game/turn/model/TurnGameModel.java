package com.pigdroid.game.turn.model;

import com.pigdroid.game.model.GameModel;
import com.pigdroid.game.model.Player;

public class TurnGameModel extends GameModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer currentTurn = 0;
	private Integer turnCount = 0;
	private Integer roundCount = 0;
	private Boolean moved = Boolean.FALSE;

	public void setMoved(Boolean b) {
		this.moved = b;
	}

	public boolean isMoved() {
		return moved;
	}
	
	public Boolean getMoved() {
		return moved;
	}

	public Integer getRoundCount() {
		return roundCount;
	}
	
	public Player getCurrentPlayer() {
		Player ret = null;
		if (currentTurn > -1 && currentTurn < getPlayers().size()) {
			ret = getPlayers().get(currentTurn);
		}
		return ret;
	}
	
	public void setCurrentTurn(Integer currentTurn) {
		this.currentTurn = currentTurn;
	}
	
	public void setTurnCount(Integer turnCount) {
		this.turnCount = turnCount;
	}
	
	public void setRoundCount(Integer roundCount) {
		this.roundCount = roundCount;
	}
	
	public Integer getTurnCount() {
		return turnCount;
	}
	
	public Integer getCurrentTurn() {
		return currentTurn;
	}

}
