package com.pigdroid.game.board.tile.model;


public class IntTileLayer extends TileLayer<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IntTileLayer() {
		super();
	}
	
	public IntTileLayer(Integer width, Integer height, Integer filler) {
		super(width, height, filler);
	}

	public IntTileLayer(Integer width, Integer height) {
		super(width, height);
	}

	@Override
	public Integer get(int x, int y) {
		Object obj = super.get(x, y);
		if (obj == null) {
			return null;
		}
		if (obj instanceof Long) {
			return ((Long) obj).intValue();
		}
		return  (Integer) obj;
		
	}

	@Override
	public String toString() {
		return "IntTileLayer [getArray()=" + getArray() + ", getWidth()="
				+ getWidth() + ", getHeight()=" + getHeight() + ", getId()="
				+ getId() + "]";
	}

}