package com.pigdroid.game.board.tile.dots.model;

import com.pigdroid.game.board.tile.model.TileBoardGameSelection;

public class DotsSelection extends TileBoardGameSelection {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8007807624605391214L;
	private boolean close = false;

	public DotsSelection() {
	}
	
	public DotsSelection(boolean close) {
		this.close = close;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean jump) {
		this.close = jump;
	}

}
