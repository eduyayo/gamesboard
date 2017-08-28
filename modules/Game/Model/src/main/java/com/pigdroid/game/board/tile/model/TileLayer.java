package com.pigdroid.game.board.tile.model;

import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.model.Layer;

public class TileLayer<T> extends Layer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map <Integer, T> array = new TreeMap<Integer, T>();

	public TileLayer() {
	}
	
	private int key(int x, int y) {
		return x + y * getWidth();
	}

	public TileLayer(Integer width, Integer height) {
		super(width, height);
	}
	
	public TileLayer(Integer width, Integer height, T filler) {
		super(width, height);
		clear(filler);
	}
	
	public void clear(T filler) {
		int width = getWidth();
		int height = getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				array.put(key(x, y), filler);
			}
		}
	}

	public T get(int x, int y) {
		return (T)array.get(key(x, y));
	}
	
	public T set(int x, int y, T in) {
		return array.put(key(x, y), in);
	}
	
	public Map<Integer, T> getArray() {
		return array;
	}
	
	public void setArray(Map<Integer, T> array) {
		this.array = array;
	}

}
