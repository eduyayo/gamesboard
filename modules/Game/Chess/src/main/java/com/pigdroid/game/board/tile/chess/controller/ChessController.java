package com.pigdroid.game.board.tile.chess.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.tile.chess.model.ChessModel;
import com.pigdroid.game.board.tile.chess.model.ChessSelection;
import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.resource.ResourceProvider;

public class ChessController extends TileBoardGameController<Integer, IntTileLayer, ChessModel> {


	@Override
	public Map<Integer, GameSelection> getSelectables() {
		int playerIndex = getModel().getCurrentTurn();
		if (isPlayerLocal(playerIndex)) {
			return getSelectables(playerIndex);
		} else {
			return Collections.emptyMap();
		}
	}
	
	protected Map<Integer, GameSelection> getSelectables(int playerIndex) {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		ChessModel game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Player currentPlayer = game.getCurrentPlayer();
		if (currentPlayer != null) {
			String playerName = currentPlayer.getName();
			if (currentPlayer instanceof HumanPlayer) {
				playerName = ((HumanPlayer) currentPlayer).getEmail();
			}
			boolean playerCaps = isCapsPlayerIndex(playerIndex);
			List<GameSelection> selections = getSelections();
			TileBoardGameSelection currentSelection = 
					(TileBoardGameSelection) (selections.size() == 1 
					? selections.get(0) : null);
			if (currentSelection == null) {
				int checkLevel = ChessHelper.getCheckLevel(layer, playerCaps, null); //TODO cache the list of escapes!!
				if (checkLevel == ChessHelper.CHECK_LEVEL_CHECK) {
					ChessSelection selection = createKingSelection(layer, playerCaps, playerName); //TODO fill all escapes not only king movements
					ret.put(getZIndex(selection.getX(), selection.getY(), 1), selection);
				} else {
					// current players pieces that can be selected
					for (ChessSelection selection : ChessHelper.getCanMoves(layer, playerCaps)) {
						selection.setPlayerName(playerName);
						ret.put(getZIndex(selection.getX(), selection.getY(), 1), selection);
					}
				}
			} else {
				// places where to move the selected piece
				TileBoardGameSelection selection = (TileBoardGameSelection) currentSelection;
				int sx = selection.getX();
				int sy = selection.getY();
				// Let's assume that player 0 plays bottom to top
				for (ChessSelection current : ChessHelper.getCanMoveTos(sx, sy, layer, true)) {
					current.setPlayerName(playerName);
					ret.put(getZIndex(current.getX(), current.getY(), 1), current);
				}
			}
		}
		return ret;
	}

	private ChessSelection createKingSelection(
			IntTileLayer layer,
			boolean playerCaps, String playerName) {
		ChessSelection ret = null;
		int width = layer.getWidth();
		int height = layer.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Integer piece = layer.get(x, y);
				if (((playerCaps && ChessHelper.isCap(piece))
						|| !playerCaps && !ChessHelper.isCap(piece)) 
					&& ChessHelper.isKing(piece)) {
					ret = ChessHelper.createSelection(playerName, (long) piece, x, y, 1);
					break;
				}
			}
		}
		return ret;
	}

	private boolean isCapsPlayerIndex(int playerIndex) {
		return playerIndex == 0 ? true : false;
	}

	@Override
	protected void move() {
		ChessModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		int size = selections.size();
		TileBoardGameSelection src = (TileBoardGameSelection) selections.get(size - 2);
		TileBoardGameSelection dst = (TileBoardGameSelection) selections.get(size - 1);
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Integer piece = (Integer) layer.get(src.getX(), src.getY());
		layer.set(src.getX(), src.getY(), ChessModel.BLANK);
		
		//check for crowning a pawn
		//TODO
//		if (!isQueen(piece) && (dst.getY() == 0 || dst.getY() == getHeight() - 1)) {
//			piece++;
//		}
		
		layer.set(dst.getX(), dst.getY(), piece); //TODO Calc the replacement with queen/horse/bishop!
		boolean moved = true;
		super.move();
		game.setMoved(moved);
	}

	@Override
	public boolean isReadyToStart() {
		ChessModel game = getModel();
		int present = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStatus().equals(PlayerStatusEnum.PRESENT)) {
				present ++;
			}
		}
		return present == 2;
	}

	@Override
	protected ChessModel createModel() {
		ChessModel game = new ChessModel();
		game.addLayer();
		int index = game.addLayer();
		ChessHelper.getInitialLayer(game.getLayer(index));
		return game;
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		ChessModel game = getModel();
		// First we get those that can jump
		String playerName = getCurrentPlayerEmail();
		for (int z = 0; z < 2; z ++) {
			IntTileLayer layer = (IntTileLayer) game.getLayer(z);
			int maxx = layer.getWidth();
			int maxy = layer.getHeight();
			for (int x = 0; x < maxx; x++) {
				for (int y = 0; y < maxy; y++) {
					int tile = ((Integer) layer.get(x, y)).intValue();
					if (tile != ChessModel.BLANK) {
						int zindex = getZIndex(x, y, z);
						ret.put(zindex, ChessHelper.createSelection(playerName, tile, x, y, z));
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * white spaces are not selectables
	 * white spaces are in the right hand for both players
	 * so 0,0 is white and cannot be used
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isUnusable(int x, int y) {
		boolean ret = true;
		if (x > -1 && x < 8 && y > -1 && y < 8) {
			boolean xEven = x % 2 == 0;
			boolean yEven = y % 2 == 0;
			if (xEven != yEven) {
				ret  = false;
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
		ChessModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		ret = selections.size() == 2;
		return ret;
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		super.onInitResourceProvider(resourceProvider);
		resourceProvider.addResource(null, ChessModel.BLANK);
		
		resourceProvider.addResource("com/pigdroid/game/checkers/board/black_tile.svg", ChessModel.BK_BLACK);
		resourceProvider.addResource("com/pigdroid/game/checkers/board/white_tile.svg", ChessModel.BK_WHITE);

		resourceProvider.addResource("com/pigdroid/game/chess/black_bishop.svg",	ChessModel.BISHOP_BLACK);
		resourceProvider.addResource("com/pigdroid/game/chess/black_horse.svg",		ChessModel.HORSE_BLACK);
		resourceProvider.addResource("com/pigdroid/game/chess/black_king.svg",		ChessModel.KING_BLACK);
		resourceProvider.addResource("com/pigdroid/game/chess/black_pawn.svg",		ChessModel.PAWN_BLACK);
		resourceProvider.addResource("com/pigdroid/game/chess/black_queen.svg",		ChessModel.QUEEN_BLACK);
		resourceProvider.addResource("com/pigdroid/game/chess/black_tower.svg",		ChessModel.TOWER_BLACK);
		
		resourceProvider.addResource("com/pigdroid/game/chess/white_bishop.svg",	ChessModel.BISHOP_WHITE);
		resourceProvider.addResource("com/pigdroid/game/chess/white_horse.svg",		ChessModel.HORSE_WHITE);
		resourceProvider.addResource("com/pigdroid/game/chess/white_king.svg",		ChessModel.KING_WHITE);
		resourceProvider.addResource("com/pigdroid/game/chess/white_pawn.svg",		ChessModel.PAWN_WHITE);
		resourceProvider.addResource("com/pigdroid/game/chess/white_queen.svg",		ChessModel.QUEEN_WHITE);
		resourceProvider.addResource("com/pigdroid/game/chess/white_tower.svg",		ChessModel.TOWER_WHITE);
		
	}

	@Override
	public void select(GameSelection... gameSelections) {
		super.select(gameSelections);
	}

	@Override
	protected List<GameSelection> getSelections() {
		ChessModel game = getModel();
		List<GameSelection> ss = game.getSelections();
		return ss;
	}
	
	@Override
	public boolean isWinner(Player currentPlayer) {
		ChessModel model = getModel();
		int playerIndex = model.getCurrentTurn() + 1;
		return getSelectables(playerIndex % model.getPlayerCount()).isEmpty();
	}

	@Override
	public boolean isNeverReadyToStart() {
		ChessModel model = getModel();
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
	
}
