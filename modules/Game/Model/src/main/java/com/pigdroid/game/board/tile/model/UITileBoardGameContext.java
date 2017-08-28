package com.pigdroid.game.board.tile.model;

import java.util.List;
import java.util.Map;

import com.pigdroid.game.board.model.UIBoardGameContext;
import com.pigdroid.game.model.GameSelection;

public class UITileBoardGameContext extends UIBoardGameContext {

	private int width;
	private int height;
	private int layerCount;

	public UITileBoardGameContext(
			Map<Integer, GameSelection> uiElements,
			Map<Integer, GameSelection> selectables,
			List<GameSelection> selections, 
			int width, 
			int height, int layerCount) {
		super(uiElements, selectables, selections);
		this.width = width;
		this.height = height;
		this.layerCount = layerCount;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLayerCount() {
		return layerCount;
	}

}

