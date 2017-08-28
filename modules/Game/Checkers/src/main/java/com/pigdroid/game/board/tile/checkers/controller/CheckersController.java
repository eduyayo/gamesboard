package com.pigdroid.game.board.tile.checkers.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.tile.checkers.model.CheckersModel;
import com.pigdroid.game.board.tile.checkers.model.CheckersSelection;
import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.resource.ResourceProvider;

public class CheckersController extends TileBoardGameController<Integer, IntTileLayer, CheckersModel> {

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
		CheckersModel game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Player currentPlayer = game.getCurrentPlayer();
		if (currentPlayer != null) {
			String playerName = currentPlayer.getName();
			if (currentPlayer instanceof HumanPlayer) {
				playerName = ((HumanPlayer) currentPlayer).getEmail();
			}
			List<GameSelection> selections = getSelections();
//			TileBoardGameSelection currentSelection = 
//			(TileBoardGameSelection) (selections.size() == 1 
//			? selections.get(0) : null);
			TileBoardGameSelection currentSelection = 
				(TileBoardGameSelection) (selections.size() > 0 
				? selections.get(selections.size() - 1) : null);
			if (currentSelection == null) {
				// current players checkers that can be selected
				int pawn = playerIndex == 0 ? CheckersModel.PAWN_WHITE : CheckersModel.PAWN_BLACK;
				int queen = playerIndex == 0 ? CheckersModel.QUEEN_WHITE : CheckersModel.QUEEN_BLACK;
				int maxx = layer.getWidth();
				int maxy = layer.getHeight();
				// first we get those that can jump
				// Remember: layer is 1, it's important for zsort
				for (int x = 0; x < maxx; x++) {
					for (int y = 0; y < maxy; y++) {
						int tile = (Integer) layer.get(x, y);
						if ((tile == pawn || tile == queen) && canJump(layer, x, y, tile, playerIndex)) {
							int zindex = getZIndex(x, y, 1);
							ret.put(zindex, createSelection(playerName, tile, x, y));
						}
					}
				}
				if (ret.isEmpty()) {
					//now those that can move and not jump
					for (int x = 0; x < maxx; x++) {
						for (int y = 0; y < maxy; y++) {
							int tile = (Integer) layer.get(x, y);
							if ((tile == pawn || tile == queen) && canMove(layer, x, y, tile, playerIndex)) {
								int zindex = getZIndex(x, y, 1);
								ret.put(zindex, createSelection(playerName, tile, x, y));
							}
						}
					}
				}
			} else {
				// places where to move the selected piece
				TileBoardGameSelection selection = (TileBoardGameSelection) currentSelection;
				int sx = selection.getX();
				int sy = selection.getY();
				int spiece = selection.getModelId().intValue();
				// Let's assume that player 0 plays bottom to top
				fillJumpPlaces(playerIndex, layer, sx, sy, spiece, ret);
				if (ret.size() == 0) {
					fillMovePlaces(playerIndex, layer, sx, sy, spiece, ret);
				}
			}
		}
		return ret;
	}

	private void fillMovePlaces(int playerIndex, IntTileLayer layer, int x, int y,
			int spiece, Map<Integer, GameSelection> ret) {
		int destY = playerIndex == 0 ? y - 1 : y + 1;
		fillMovePlace(layer, x - 1, ret, destY);
		fillMovePlace(layer, x + 1, ret, destY);
		if (isQueen(spiece)) {
			destY = playerIndex == 1 ? y - 1 : y + 1;
			fillMovePlace(layer, x - 1, ret, destY);
			fillMovePlace(layer, x + 1, ret, destY);
		}
	}

	private void fillMovePlace(IntTileLayer layer, int x, Map<Integer, GameSelection> ret, int destY) {
		if (!isUnusable(x, destY) && ((Integer) layer.get(x, destY)) == CheckersModel.BLANK) {
			Player player = getModel().getCurrentPlayer();
			String playerName = player instanceof HumanPlayer ? ((HumanPlayer) player).getEmail() : player.getName();
			TileBoardGameSelection value = createSelection(playerName, (long)CheckersModel.BLANK, x, destY);
			ret.put(getZIndex(x, destY, 1), value );
		}
	}

	private boolean isQueen(int spiece) {
		return CheckersModel.QUEEN_BLACK == spiece || CheckersModel.QUEEN_WHITE == spiece;
	}

	private void fillJumpPlaces(int playerIndex, IntTileLayer layer, int x, int y,
			int spiece, Map<Integer, GameSelection> ret) {
		int add = 1;
		if (playerIndex == 0) {
			add = -1;
		}
		int add2 = add << 1;		
		fillJumpTo(layer, x + 1, y + add, x + 2, y + add2, playerIndex, ret);
		fillJumpTo(layer, x - 1, y + add, x - 2, y + add2, playerIndex, ret);
		if (isQueen(spiece)) {
			add = -add;
			add2 = add << 1;
			fillJumpTo(layer, x + 1, y + add, x + 2, y + add2, playerIndex, ret);
			fillJumpTo(layer, x - 1, y + add, x - 2, y + add2, playerIndex, ret);
		}
	}

	private void fillJumpTo(IntTileLayer layer, int opx, int opy, int dx, int dy,
			int playerIndex, Map<Integer, GameSelection> ret) {
		if (canJumpTo(layer, opx, opy, dx, dy, playerIndex)) {
			Player player = getModel().getCurrentPlayer();
			String playerName = player instanceof HumanPlayer ? ((HumanPlayer) player).getEmail() : player.getName();
			TileBoardGameSelection value = createSelection(playerName, (long)CheckersModel.BLANK, dx, dy, true);
			ret.put(getZIndex(dx, dy, 1), value);
		}
	}

	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y) {
		return createSelection(playerName, tile, x, y, false);
	}
	
	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y, boolean isJump) {
		TileBoardGameSelection selection = new CheckersSelection(isJump);
		selection.setPlayerName(playerName);
		selection.setLayer(1);
		selection.setModelId((long) tile);
		selection.setX(x);
		selection.setY(y);
		return selection;
	}

	private boolean canMove(IntTileLayer layer, int x, int y, int tile, int playerIndex) {
		boolean ret = false;
		// Let's assume that player 0 plays bottom to top
		int destY = playerIndex == 0 ? y - 1 : y + 1;
		ret = canMoveTo(layer, x - 1, destY) || canMoveTo(layer, x + 1, destY);
		if (!ret && isQueen(tile)) {
			destY =  playerIndex == 0 ? y + 1 : y - 1;
			ret = canMoveTo(layer, x - 1, destY) || canMoveTo(layer, x + 1, destY);
		}
		return ret;
	}

	private boolean canMoveTo(IntTileLayer layer, int x, int destY) {
		boolean ret = false;
		if (!isUnusable(x, destY)) {
			ret = ((Integer)layer.get(x, destY)) == CheckersModel.BLANK;
		}
		return ret;
	}

	private boolean canJump(TileBoardGameSelection selection) {
		CheckersModel game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		return canJump(layer, selection.getX(), selection.getY(), layer.get(selection.getX(), selection.getY()), game.getCurrentTurn());
	}
	
	private boolean canJump(IntTileLayer layer, int x, int y, int tile, int playerIndex) {
		boolean ret = false;
		int add = 1;
		if (playerIndex == 0) {
			add = -1;
		}
		int add2 = add << 1;
		ret = canJumpTo(layer, x + 1, y + add, x + 2, y + add2, playerIndex);
		ret = ret ? ret : canJumpTo(layer, x - 1, y + add, x - 2, y + add2, playerIndex);
		if (!ret && isQueen(tile)) {
			add = -add;
			add2 = add << 1;
			ret = canJumpTo(layer, x + 1, y + add, x + 2, y + add2, playerIndex);
			ret = ret ? ret : canJumpTo(layer, x - 1, y + add, x - 2, y + add2, playerIndex);
		}
		return ret;
	}

	private boolean canJumpTo(IntTileLayer layer, int opx, int opy, int dx, int dy, int playerIndex) {
		boolean ret = false;
		if (!isUnusable(dx, dy) && isOpponent(playerIndex, ((Integer)layer.get(opx, opy))) && canMoveTo(layer, dx, dy)) {
			ret = true;
		}
		return ret;
	}
	
	private boolean isOpponent(int playerIndex, int piece) {
		boolean ret = false;
		if (piece != CheckersModel.BLANK) {
			ret = playerIndex != (piece - 1) / 2;
		}
		return ret;
	}
	
	@Override
	protected void move() {
		CheckersModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		int size = selections.size();
		TileBoardGameSelection src = (TileBoardGameSelection) selections.get(size - 2);
		TileBoardGameSelection dst = (TileBoardGameSelection) selections.get(size - 1);
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Integer piece = (Integer) layer.get(src.getX(), src.getY());
		layer.set(src.getX(), src.getY(), CheckersModel.BLANK);
		
		//check for new queen
		if (!isQueen(piece) && (dst.getY() == 0 || dst.getY() == getHeight() - 1)) {
			piece++;
		}
		
		layer.set(dst.getX(), dst.getY(), piece);
		boolean moved = true;
		if (selections.size() > 1) {
			CheckersSelection selection = (CheckersSelection) selections.get(selections.size() - 1);
			if (selection.isJump()) {
				//Remove the jumped over piece
				int jx = (src.getX() + dst.getX()) / 2;
				int jy = (src.getY() + dst.getY()) / 2;
				layer.set(jx, jy, CheckersModel.BLANK);
				if (canJump(selection)) {
					moved = false; //Still can jump
					CheckersSelection selectionToKeep = null;
					selectionToKeep = selection;
					selectionToKeep.setModelId((long) layer.get(selectionToKeep.getX(), selectionToKeep.getY()));
					game.setSelectionToKeep(selectionToKeep);
				}
			}
		}
		super.move();
		game.setMoved(moved);
	}

	@Override
	public boolean isReadyToStart() {
		CheckersModel game = getModel();
		int present = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStatus().equals(PlayerStatusEnum.PRESENT)) {
				present ++;
			}
		}
		return present == 2;
	}

	@Override
	protected CheckersModel createModel() {
		CheckersModel game = new CheckersModel();
		game.addLayer();
		game.addLayer();
		initCheckers(game);
		return game;
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		CheckersModel game = getModel();
		// First we get those that can jump
		for (int z = 0; z < 2; z ++) {
			IntTileLayer layer = (IntTileLayer) game.getLayer(z);
			int maxx = layer.getWidth();
			int maxy = layer.getHeight();
			for (int x = 0; x < maxx; x++) {
				for (int y = 0; y < maxy; y++) {
					int tile = ((Integer) layer.get(x, y)).intValue();
					if (tile != CheckersModel.BLANK) {
						int zindex = getZIndex(x, y, z);
						ret.put(zindex, createSelection(null, tile, x, y));
					}
				}
			}
		}
		return ret;
	}
	
	private void initCheckers(CheckersModel game) {
		IntTileLayer layer = (IntTileLayer) game.getLayer(0);
		// Iterating for in case of user resets the board
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				layer.set(x, y, CheckersModel.BK_BLACK);
				if (isUnusable(x, y)) {
					layer.set(x, y, CheckersModel.BK_WHITE);
				}
			}
		}
		layer = (IntTileLayer) game.getLayer(1);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 3; y++) {
				if (!isUnusable(x, y)) {
					layer.set(x, y, CheckersModel.PAWN_BLACK);
				}
			}
		}
		for (int x = 0; x < 8; x++) {
			for (int y = 7; y > 4; y--) {
				if (!isUnusable(x, y)) {
					layer.set(x, y, CheckersModel.PAWN_WHITE);
				}
			}
		}
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
		getModel().setSelectionToKeep(null);
		super.doStartTurn();
		getModel().setMoved(false);
	}

	@Override
	protected boolean isMove() {
		boolean ret = false;
		CheckersModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		CheckersSelection lastSelection = (CheckersSelection) (selections.size() > 0 ? selections.get(selections.size() - 1) : null);
//		ret = (selections.size() == 2  && !lastSelection.isJump())
//				|| (selections.size() > 2 && lastSelection.isJump() && !canJump(lastSelection));
		
//		System.out.println("count " + selections.size());
		
//		if (selections.size()  == 2) {
//			if (lastSelection.isJump()) {
//				if (canJump(lastSelection)) {
//					ret = false;
//				} else {
//					ret = true;
//				}
//			} else {
//				ret = true;
//			}
//		} else if (selections.size() > 2) {
//			if (canJump(lastSelection)) {
//				ret = false;
//			} else {
//				ret = true;
//			}
//		}
		ret = selections.size() > 1;
		return ret;
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		super.onInitResourceProvider(resourceProvider);
		resourceProvider.addResource(null, CheckersModel.BLANK);
		resourceProvider.addResource("com/pigdroid/game/checkers/board/black_tile.svg", CheckersModel.BK_BLACK);
		resourceProvider.addResource("com/pigdroid/game/checkers/board/white_tile.svg", CheckersModel.BK_WHITE);
		resourceProvider.addResource("com/pigdroid/game/checkers/red_queen.svg", CheckersModel.QUEEN_WHITE);
		resourceProvider.addResource("com/pigdroid/game/checkers/red.svg", CheckersModel.PAWN_WHITE);
//		addResource("com/pigdroid/game/chess/black_horse.svg.xml", CheckersModel.PAWN_WHITE);
		resourceProvider.addResource("com/pigdroid/game/checkers/blue_queen.svg", CheckersModel.QUEEN_BLACK);
		resourceProvider.addResource("com/pigdroid/game/checkers/blue.svg", CheckersModel.PAWN_BLACK);
	}

	@Override
	public void select(GameSelection... gameSelections) {
		getModel().setSelectionToKeep(null);
		super.select(gameSelections);
	}

	@Override
	protected List<GameSelection> getSelections() {
		CheckersModel game = getModel();			
		List<GameSelection> ss = null;
		if (game.getSelectionToKeep() != null) {
			ss = new ArrayList<GameSelection>(1);
			ss.add(game.getSelectionToKeep());
		} else {
			ss = super.getSelections();
		}
		return ss;
	}
	
	@Override
	protected void clearSelections(boolean send) {
		super.clearSelections(send);
		if (send) {
			getModel().getSelections().clear();
		}
	}
	
	@Override
	public boolean isWinner(Player currentPlayer) {
		CheckersModel model = getModel();
		int playerIndex = model.getCurrentTurn() + 1;
		return getSelectables(playerIndex % model.getPlayerCount()).isEmpty();
	}
	
	@Override
	public boolean isNeverReadyToStart() {
		CheckersModel model = getModel();
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
