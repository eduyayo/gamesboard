package com.pigdroid.game.board.tile.papersoccer.model;

import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;
import com.pigdroid.game.board.tile.papersoccer.controller.PaperSoccerHelper;

public class PaperSoccerModel extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int BLANK	= PaperSoccerHelper.BLANK;
	public static final int RED		= PaperSoccerHelper.RED;
	public static final int BLUE	= PaperSoccerHelper.BLUE;
	public static final int FRONT	= PaperSoccerHelper.BLUE + 1;
	public static final int BACK	= PaperSoccerHelper.BLUE + 2;
	
	private static final long serialVersionUID = 1L;
	
	private Boolean moved = Boolean.FALSE;

	public PaperSoccerModel() {
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
