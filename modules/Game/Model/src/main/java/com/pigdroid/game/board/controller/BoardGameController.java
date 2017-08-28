package com.pigdroid.game.board.controller;

import com.pigdroid.game.board.model.BoardGameModel;
import com.pigdroid.game.board.model.BoardGameSelection;
import com.pigdroid.game.board.model.Layer;
import com.pigdroid.game.board.model.UIBoardGameContext;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.turn.controller.TurnGameController;

public abstract class BoardGameController<TileType, LayerType extends Layer<TileType>, Game extends BoardGameModel<TileType, LayerType>> extends TurnGameController<Game> {

	private int height;
	private int width;
	private int layerCount;

	@Override
	public int getIndex(GameSelection gameSelection) {
		BoardGameSelection selection = (BoardGameSelection) gameSelection;
		if (selection.getX() == null) {
			throw new RuntimeException("Selection.getX is null.");
		}
		if (selection.getY() == null) {
			throw new RuntimeException("Selection.getY is null.");
		}
		if (selection.getLayer() == null) {
			throw new RuntimeException("Selection.getLayer is null.");
		}
		return getZIndex(selection.getX(), selection.getY(), selection.getLayer());
	}
	
	public int getZIndex(int x, int y, int z) {
		return x + y * width + z * width * height;
	}
	
	public int getLayerCount() {
		return layerCount;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	protected UIGameContext createUIContext() {
		Game game = getModel();
		return new UIBoardGameContext(getUIElements(), getSelectables(), getSelections());
	}
	
	@Override
	protected void onInitModel(Game game) {
		this.width = game.getWidth();
		this.height = game.getHeight();
		this.layerCount = game.getLayerCount();
		super.onInitModel(game);
	}
	
}
