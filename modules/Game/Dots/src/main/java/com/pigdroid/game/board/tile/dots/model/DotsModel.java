package com.pigdroid.game.board.tile.dots.model;

import com.pigdroid.game.board.tile.dots.controller.DotsHelper;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;

public class DotsModel extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int EMPTY		= DotsHelper.EMPTY;
	public static final int DOT			= DotsHelper.DOT;
	public static final int MARK		= DotsHelper.MARK;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean moved = Boolean.FALSE;

	public DotsModel() {
		super(17, 17, EMPTY);
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
		return new IntTileLayer(17, 17, 0);
	}

}
