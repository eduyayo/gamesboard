package com.pigdroid.game.board.tile.model;

import com.pigdroid.game.board.model.BoardGameModel;

public abstract class TileBoardGameModel<T, L extends TileLayer<T>> extends BoardGameModel<T, L> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int TILE_SELECTABLE = Integer.MIN_VALUE;
	public static final int TILE_SELECTED 	= Integer.MIN_VALUE + 1;
	
	private T filler;
	
	public TileBoardGameModel(int width, int height, T filler) {
		super(width, height);
		this.filler = filler;
	}
	
	public TileBoardGameModel() {
		
	}

	public T getElement(int x, int y, int z) {
		return getLayers().get(z).get(x, y);
	}

	public T getFiller() {
		return filler;
	}

	public void setFiller(T typeOf) {
		this.filler = typeOf;
	}
	
}