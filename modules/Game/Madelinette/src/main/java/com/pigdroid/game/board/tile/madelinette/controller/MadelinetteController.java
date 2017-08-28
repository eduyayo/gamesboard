package com.pigdroid.game.board.tile.madelinette.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.madelinette.model.MadelinetteModel;
import com.pigdroid.game.board.tile.madelinette.model.MadelinetteSelection;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.resource.ResourceProvider;

public class MadelinetteController extends TileBoardGameController<Integer, IntTileLayer, MadelinetteModel> {

	private static int[][] BOARD_DEF = {
		{MadelinetteModel.BLUE,  MadelinetteModel.BLOCKED, MadelinetteModel.RED},
		{MadelinetteModel.RED,  MadelinetteModel.BLANK, MadelinetteModel.BLUE},
		{MadelinetteModel.BLUE,  MadelinetteModel.BLOCKED, MadelinetteModel.RED}
	};
	
	private static final int [][] CONNECTION_MATRIX = {
	/*	 0  1  2  3  4  5  6  7  8*/
		{0, 0, 0, 1, 1, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 1, 1, 0, 0, 0},
		{1, 0, 0, 0, 1, 0, 1, 0, 0},
		{1, 0, 1, 1, 0, 1, 1, 0, 1},
		{0, 0, 1, 0, 1, 0, 0, 0, 1},
		{0, 0, 0, 1, 1, 0, 0, 0, 1},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 1, 1, 1, 0, 0}
	};
	
	@Override
	public Map<Integer, GameSelection> getSelectables() {
		int playerIndex = getModel().getCurrentTurn();
		if (isPlayerLocal(playerIndex) && !isGameEnd(getModel().getCurrentPlayer())) {
			return getSelectables(playerIndex);
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	protected Map<Integer, GameSelection> getSelectables(int playerIndex) {
		Map<Integer, GameSelection> ret = new HashMap<Integer, GameSelection>();
		MadelinetteModel game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		int playerPawn = playerIndex + MadelinetteModel.RED;
		Player currentPlayer = game.getCurrentPlayer();
		if (currentPlayer != null) {
			String playerName = currentPlayer.getName();
			if (currentPlayer instanceof HumanPlayer) {
				playerName = ((HumanPlayer) currentPlayer).getEmail();
			}
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					if (canMove(x, y, layer, playerPawn)) {
						int z = getZIndex(x, y, 1);
						ret.put(z, createSelection(playerName, playerPawn, x, y));
					}
				}
			}
		}
		return ret;
	}
	
	private boolean canMove(int x, int y, IntTileLayer layer, int playerPawn) {
		boolean ret = false;
		if (layer.get(x,  y) == playerPawn) {
			int [] coords = getBlanckCoords(layer);
			Integer width = layer.getWidth();
			int toNode = coords[0] + coords[1] * width;
			int fromNode = x + y * width;
			if (CONNECTION_MATRIX[fromNode][toNode] == 1) {
				ret  = true;
			} else {
				ret = false;
			}
		} else {
			ret = false;
		}
		return ret;
	}

	private int [] getBlanckCoords(IntTileLayer layer) {
		int [] ret = {0, 0};
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (layer.get(x, y) == MadelinetteModel.BLANK) {
					ret[0] = x;
					ret[1] = y;
					return ret;
				}
			}
		}
		return ret;
	}

	private GameSelection createSelection(String playerName, long tile, Integer x, Integer y) {
		MadelinetteSelection selection = new MadelinetteSelection();
		selection.setPlayerName(playerName);
		selection.setLayer(1);
		selection.setModelId((long) tile);
		selection.setX(x);
		selection.setY(y);
		return selection;
	}

	@Override
	public boolean isTurnDone() {
		return getModel().getMoved();
	}

	@Override
	public boolean isReadyToStart() {
		MadelinetteModel game = getModel();
		int present = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStatus().equals(PlayerStatusEnum.PRESENT)) {
				present ++;
			}
		}
		return present == 2;
	}

	@Override
	protected MadelinetteModel createModel() {
		MadelinetteModel game = new MadelinetteModel();
		game.addLayer();
		game.addLayer();
		initMadelinette(game);
		return game;
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		MadelinetteModel game = getModel();
		// First we get those that can jump
		String playerName = getCurrentPlayerEmail();
		for (int z = 0; z < 2; z ++) {
			IntTileLayer layer = (IntTileLayer) game.getLayer(z);
			int maxx = layer.getWidth();
			int maxy = layer.getHeight();
			for (int x = 0; x < maxx; x++) {
				for (int y = 0; y < maxy; y++) {
					int tile = ((Integer) layer.get(x, y)).intValue();
					if (tile != MadelinetteModel.BLANK) {
						int zindex = getZIndex(x, y, z);
						ret.put(zindex, createSelection(playerName, tile, x, y));
					}
				}
			}
		}
		return ret;
	}

	private void initMadelinette(MadelinetteModel game) {
		IntTileLayer layer = (IntTileLayer) game.getLayer(0);
		layer.set(0, 0, MadelinetteModel.BACK_TL);
		layer.set(1, 0, MadelinetteModel.BACK_T);
		layer.set(2, 0, MadelinetteModel.BACK_TR);

		layer.set(0, 1, MadelinetteModel.BACK_ML);
		layer.set(1, 1, MadelinetteModel.BACK_M);
		layer.set(2, 1, MadelinetteModel.BACK_MR);

		layer.set(0, 2, MadelinetteModel.BACK_BL);
		layer.set(1, 2, MadelinetteModel.BACK_B);
		layer.set(2, 2, MadelinetteModel.BACK_BR);
		
		layer = (IntTileLayer) game.getLayer(1);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				layer.set(x, y, BOARD_DEF[y][x]); 
			}
		}
	}

	@Override
	protected boolean isMove() {
		return getSelections().size() == 1;
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		super.onInitResourceProvider(resourceProvider);
		
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/top_left.svg", MadelinetteModel.BACK_TL);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/top.svg", MadelinetteModel.BACK_T);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/top_right.svg", MadelinetteModel.BACK_TR);
		
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/middle_left.svg", MadelinetteModel.BACK_ML);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/middle.svg", MadelinetteModel.BACK_M);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/middle_right.svg", MadelinetteModel.BACK_MR);
		
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/bottom_left.svg", MadelinetteModel.BACK_BL);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/bottom.svg", MadelinetteModel.BACK_B);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/bottom_right.svg", MadelinetteModel.BACK_BR);

		resourceProvider.addResource(null, MadelinetteModel.BLANK);
		resourceProvider.addResource(null, MadelinetteModel.BLOCKED);
		

		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/red.svg", MadelinetteModel.RED);
		resourceProvider.addResource("com/pigdroid/game/board/tile/madelinette/blue.svg", MadelinetteModel.BLUE);
	}
	
	@Override
	public void doStartTurn() {
		super.doStartTurn();
		getModel().setMoved(false);
	}

	@Override
	public boolean isNeverReadyToStart() {
		MadelinetteModel model = getModel();
		int left = 0;
		for (Player player : model.getPlayers()) {
			if (PlayerStatusEnum.GONE.equals(player.getStatus())) {
				left ++;
			}
		}
		return left > 0;
	}
	
	@Override
	public boolean isWinner(Player currentPlayer) {
		for (Player player : getModel().getPlayers()) {
			if (player != currentPlayer) {
				if (isLoser(player)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isLoser(Player currentPlayer) {
		int index = getPlayerIndex(((HumanPlayer) currentPlayer).getEmail());
		return getSelectables(index).isEmpty();
	}

	@Override
	protected void move() {
		MadelinetteModel model = getModel();
		IntTileLayer layer = model.getLayer(1);
		MadelinetteSelection selection = (MadelinetteSelection) model.getSelections().get(0);
		int [] to = getBlanckCoords(layer);
		int piece = layer.get(selection.getX(), selection.getY());
		layer.set(selection.getX(), selection.getY(), MadelinetteModel.BLANK);
		layer.set(to[0], to[1], piece);
		super.move();
		model.setMoved(true);
	}
	
	@Override
	protected UIGameContext createUIContext() {
		UIGameContext ctx = super.createUIContext();
		ctx.setRotationDegreesPerPlayer(0);
		return ctx;
	}
	
}
