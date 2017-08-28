package com.pigdroid.game.board.tile.controller;

import com.pigdroid.game.board.controller.BoardGameController;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;
import com.pigdroid.game.board.tile.model.TileLayer;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.resource.ResourceProvider;

public abstract class TileBoardGameController<T, L extends TileLayer<T>, G extends TileBoardGameModel<T, L>> extends BoardGameController<T, L, G> {

	@Override
	protected UIGameContext createUIContext() {
		TileBoardGameModel<?, TileLayer<?>> game = (TileBoardGameModel<?, TileLayer<?>>) getModel();
		return new UITileBoardGameContext(getUIElements(), getSelectables(), getSelections(), game.getWidth(), game.getHeight(), game.getLayerCount());
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		resourceProvider.addResource("com/pigdroid/game/board/tile/selectable.svg", TileBoardGameModel.TILE_SELECTABLE);
		resourceProvider.addResource("com/pigdroid/game/board/tile/selected.svg", TileBoardGameModel.TILE_SELECTED);
	}
	
}
