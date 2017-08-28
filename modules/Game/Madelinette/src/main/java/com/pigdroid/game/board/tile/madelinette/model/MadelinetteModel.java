package com.pigdroid.game.board.tile.madelinette.model;

import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;

public class MadelinetteModel extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int BACK_TL		= 100;
	public static final int BACK_T		= 101;
	public static final int BACK_TR		= 102;
	
	public static final int BACK_ML		= 103;
	public static final int BACK_M		= 104;
	public static final int BACK_MR		= 105;
	
	public static final int BACK_BL		= 106;
	public static final int BACK_B		= 107;
	public static final int BACK_BR		= 108;
	
	public static final int BLANK		= 0;
	public static final int BLOCKED		= 1;
	public static final int RED	= 2;
	public static final int BLUE	= 3;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean moved = Boolean.FALSE;

	public MadelinetteModel() {
		super(3, 3, BLANK);
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
		return new IntTileLayer(3, 3, 0);
	}
	
}
