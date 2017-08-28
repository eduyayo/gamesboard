package com.pigdroid.game.board.tile.connect4.model;

import com.pigdroid.game.board.tile.connect4.controller.Connect4Helper;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;

public class Connect4Model extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int BLANK	= Connect4Helper.BLANK;
	public static final int RED		= Connect4Helper.RED;
	public static final int BLUE	= Connect4Helper.BLUE;
	public static final int FRONT	= Connect4Helper.BLUE + 1;
	public static final int BACK	= Connect4Helper.BLUE + 2;
	
	private static final long serialVersionUID = 1L;
	
	private Boolean moved = Boolean.FALSE;

	public Connect4Model() {
		super(7, 6, BLANK);
	}

	public void setMoved(Boolean b) {
		this.moved = b;
	}

	public boolean isMoved() {
		return moved;
	}
	
	public Boolean getMoved() {
		return moved;
	}

	@Override
	protected IntTileLayer createLayer() {
		return new IntTileLayer(7, 6, BLANK);
	}

}
