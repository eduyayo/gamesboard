package com.pigdroid.game.board.tile.connect4.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.tile.connect4.model.Connect4Model;
import com.pigdroid.game.board.tile.connect4.model.Connect4Selection;
import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.resource.ResourceProvider;

public class Connect4Controller extends TileBoardGameController<Integer, IntTileLayer, Connect4Model> {

	@Override
	public Map<Integer, GameSelection> getSelectables() {
		int playerIndex = getModel().getCurrentTurn();
		if (isPlayerLocal(playerIndex) && !isGameEnd(getModel().getCurrentPlayer())) {
			return getSelectables(playerIndex);
		} else {
			return Collections.emptyMap();
		}
	}
	
	protected Map<Integer, GameSelection> getSelectables(int playerIndex) {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		Connect4Model game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Player currentPlayer = game.getCurrentPlayer();
		if (currentPlayer != null) {
			String playerName = currentPlayer.getName();
			if (currentPlayer instanceof HumanPlayer) {
				playerName = ((HumanPlayer) currentPlayer).getEmail();
			}
			for (Connect4Selection selection : Connect4Helper.getCanMoveTos(layer)) {
				selection.setPlayerName(playerName);
				ret.put(getIndex(selection), selection);
			}
		}
		return ret;
	}

	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y) {
		return createSelection(playerName, tile, x, y, false);
	}
	
	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y, boolean isJump) {
		TileBoardGameSelection selection = new Connect4Selection();
		selection.setPlayerName(playerName);
		selection.setLayer(1);
		selection.setModelId((long) tile);
		selection.setX(x);
		selection.setY(y);
		return selection;
	}

	@Override
	protected void move() {
		Connect4Model game = getModel();
		List<GameSelection> selections = game.getSelections();
		Connect4Helper.move(game.getLayer(1), (Connect4Selection) selections.get(0), getCurrentPlayerIndex());
		super.move();
		game.setMoved(true);
	}

	@Override
	public boolean isReadyToStart() {
		Connect4Model game = getModel();
		int present = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStatus().equals(PlayerStatusEnum.PRESENT)) {
				present ++;
			}
		}
		return present == 2;
	}

	@Override
	protected Connect4Model createModel() {
		Connect4Model game = new Connect4Model();
		game.addLayer();
		int index = game.addLayer();
		Connect4Helper.getInitialLayer(game.getLayer(index));
		game.addLayer();
		game.getLayer(0).clear(Connect4Model.BACK);
		game.getLayer(2).clear(Connect4Model.FRONT);
		return game;
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		Connect4Model game = getModel();
		// First we get those that can jump
		String playerName = getCurrentPlayerEmail();
		for (int z = 0; z < 3; z ++) {
			IntTileLayer layer = (IntTileLayer) game.getLayer(z);
			int maxx = layer.getWidth();
			int maxy = layer.getHeight();
			for (int x = 0; x < maxx; x++) {
				for (int y = 0; y < maxy; y++) {
					int tile = ((Integer) layer.get(x, y)).intValue();
					if (tile != Connect4Model.BLANK) {
						int zindex = getZIndex(x, y, z);
						ret.put(zindex, createSelection(playerName, tile, x, y));
					}
				}
			}
		}
		return ret;
	}
	
	@Override
	public void doStartTurn() {
		super.doStartTurn();
		getModel().setMoved(false);
	}

	@Override
	protected boolean isMove() {
		boolean ret = false;
		Connect4Model game = getModel();
		List<GameSelection> selections = game.getSelections();
		ret = selections.size() > 0;
		return ret;
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		super.onInitResourceProvider(resourceProvider);

		resourceProvider.addResource(null, Connect4Model.BLANK);
		
		resourceProvider.addResource("com/pigdroid/game/checkers/red.svg", Connect4Model.RED);
		resourceProvider.addResource("com/pigdroid/game/checkers/blue.svg", Connect4Model.BLUE);
		resourceProvider.addResource("com/pigdroid/game/board/tile/connect4/front.svg", Connect4Model.FRONT);
		resourceProvider.addResource("com/pigdroid/game/board/tile/connect4/back.svg", Connect4Model.BACK);
	}

	@Override
	public void select(GameSelection... gameSelections) {
		super.select(gameSelections);
	}

	@Override
	protected List<GameSelection> getSelections() {
		return getModel().getSelections();
	}
	
	@Override
	public boolean isWinner(Player currentPlayer) {
		Connect4Model model = getModel();
		int playerIndex = model.getCurrentTurn();
		return Connect4Helper.isWinner(model.getLayer(1), playerIndex);
	}

	@Override
	public boolean isNeverReadyToStart() {
		Connect4Model model = getModel();
		int left = 0;
		for (Player player : model.getPlayers()) {
			if (PlayerStatusEnum.GONE.equals(player.getStatus())) {
				left ++;
			}
		}
		return left > 0;
	}
	
	@Override
	protected void doMove(Player currentPlayer) {
		if (isMove()) {
			move();
			clearSelections(getModel().getMoved());
			if (isGameEnd(currentPlayer)) {
				doEndGame(currentPlayer);
			}
		}
	}
	
	@Override
	protected UIGameContext createUIContext() {
		UIGameContext ctx = super.createUIContext();
		ctx.setRotationDegreesPerPlayer(0);
		return ctx;
	}

}
