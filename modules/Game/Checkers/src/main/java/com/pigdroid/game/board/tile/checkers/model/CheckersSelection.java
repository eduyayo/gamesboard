package com.pigdroid.game.board.tile.checkers.model;

import com.pigdroid.game.board.tile.model.TileBoardGameSelection;

public class CheckersSelection extends TileBoardGameSelection {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8007807624605391214L;
	private boolean jump = false;

	public CheckersSelection() {
	}
	
	public CheckersSelection(boolean jump) {
		this.jump = jump;
	}

	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

}
