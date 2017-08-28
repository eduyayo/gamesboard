package com.pigdroid.gfx.android;

import java.util.Map;

import android.graphics.Bitmap;

public class GFXContext {
	
	private SquareSurfaceView view;
	private Map<Integer, Bitmap> tileMap;

	public GFXContext(SquareSurfaceView view, Map<Integer, Bitmap> tileMap) {
		this.view = view;
		this.tileMap = tileMap;
	}

	public SquareSurfaceView getView() {
		return view;
	}
	
	public Map<Integer, Bitmap> getTileMap() {
		return tileMap;
	}
	
}
