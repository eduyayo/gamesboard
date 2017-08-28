package com.pigdroid.game.board.tile.dots.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.board.tile.controller.TileBoardGameController;
import com.pigdroid.game.board.tile.dots.model.DotsModel;
import com.pigdroid.game.board.tile.dots.model.DotsSelection;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.resource.ResourceProvider;

public class DotsController extends TileBoardGameController<Integer, IntTileLayer, DotsModel> {

	private static final int UI_EMPTY	= DotsModel.EMPTY;
	private static final int UI_DOT		= DotsModel.DOT;
	private static final int UI_V_MARK	= DotsModel.MARK;
	private static final int UI_H_MARK	= DotsModel.MARK + 1;
	private static final int UI_RED		= DotsModel.MARK + 2;
	private static final int UI_BLUE	= DotsModel.MARK + 3;

	private static final int UI_DOT_TL	= DotsModel.MARK + 4;
	private static final int UI_DOT_TR	= DotsModel.MARK + 5;
	private static final int UI_DOT_BL	= DotsModel.MARK + 6;
	private static final int UI_DOT_BR	= DotsModel.MARK + 7;

	private static final int UI_DOT_T	= DotsModel.MARK + 8;
	private static final int UI_DOT_R	= DotsModel.MARK + 9;
	private static final int UI_DOT_B	= DotsModel.MARK + 10;
	private static final int UI_DOT_L	= DotsModel.MARK + 11;

	@Override
	public Map<Integer, GameSelection> getSelectables() {
		int playerIndex = getModel().getCurrentTurn();
		if (isPlayerLocal(playerIndex)) {
			return getSelectables(playerIndex);
		} else {
			return Collections.emptyMap();
		}
//		int playerIndex = getModel().getCurrentTurn();
//		return getSelectables(playerIndex);
	}
	
	protected Map<Integer, GameSelection> getSelectables(int playerIndex) {
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		DotsModel game = getModel();
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		Player currentPlayer = game.getCurrentPlayer();
		if (currentPlayer != null) {
			String playerName = ((HumanPlayer) currentPlayer).getEmail();
			for (TileBoardGameSelection selection : DotsHelper.getSelectables(layer)) {
				int x = selection.getX();
				int y = selection.getY();
				int zindex = getZIndex(x, y, 1);
				ret.put(zindex, createSelection(playerName, layer.get(x, y), x, y));
			}
		}
		return ret;
	}

	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y) {
		return createSelection(playerName, tile, x, y, false);
	}
	
	private TileBoardGameSelection createSelection(String playerName, long tile, Integer x, Integer y, boolean isClose) {
		TileBoardGameSelection selection = new DotsSelection(isClose);
		selection.setPlayerName(playerName);
		selection.setLayer(1);
		selection.setModelId((long) tile);
		selection.setX(x);
		selection.setY(y);
		return selection;
	}

	@Override
	protected void move() {
		DotsModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		int size = selections.size();
		TileBoardGameSelection dst = (TileBoardGameSelection) selections.get(size - 1);
		IntTileLayer layer = (IntTileLayer) game.getLayer(1);
		layer.set(dst.getX(), dst.getY(), DotsModel.MARK);//layer.get(dst.getX(), dst.getY())
		int playerIndex = game.getCurrentTurn() + 1;
		boolean closed = DotsHelper.closeBoxes(layer, playerIndex);
		super.move();
		game.setMoved(!closed || !canMove());
	}

	private boolean canMove() {
		return DotsHelper.canMove(getModel().getLayer(1));
	}

	@Override
	public boolean isTurnDone() {
		return getModel().isMoved();
	}

	@Override
	public boolean isReadyToStart() {
		DotsModel game = getModel();
		int present = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStatus().equals(PlayerStatusEnum.PRESENT)) {
				present ++;
			}
		}
		return present == 2;
	}

	@Override
	protected DotsModel createModel() {
		DotsModel game = new DotsModel();
		game.addLayer();
		game.addLayer();
		DotsHelper.getInitialLayer(game.getLayer(1), game.getWidth(), game.getHeight());
		return game;
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
//		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
//		DotsModel game = getModel();
//		IntTileLayer layer = game.getLayer(1);
//		for (TileBoardGameSelection selection : DotsHelper.getSelectables(layer)) {
//			int x = selection.getX();
//			int y = selection.getY();
//			int zindex = getZIndex(x, y, 1);
//			ret.put(zindex, createSelection(null, layer.get(x, y), x, y));
//		}
//		return ret;
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>();
		DotsModel game = getModel();
		int width = game.getWidth();
		int height = game.getHeight();
		for (int z = 0; z < 2; z ++) {
			IntTileLayer layer = (IntTileLayer) game.getLayer(z);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int tile = ((Integer) layer.get(x, y)).intValue();
					if (tile != DotsModel.EMPTY) {
						int zindex = getZIndex(x, y, z);
						ret.put(zindex, createSelection(null, getScreenTile(tile, y), x, y));
					}
				}
			}
		}
		ret.put(getZIndex(0, 0, 1), createSelection(null, UI_DOT_TL, 0, 0));
		ret.put(getZIndex(width - 1, 0, 1), createSelection(null, UI_DOT_TR, width - 1, 0));
		ret.put(getZIndex(0, height - 1, 1), createSelection(null, UI_DOT_BL, 0, height - 1));
		ret.put(getZIndex(width - 1, height - 1, 1), createSelection(null, UI_DOT_BR, width - 1, height - 1));
		
		//TODO add the border tiles
		for (int x = 2; x < width - 1; x+=2) {
			ret.put(getZIndex(x, 0, 1), createSelection(null, UI_DOT_T, x, 0));
			ret.put(getZIndex(x, height - 1, 1), createSelection(null, UI_DOT_B, x, height - 1));
		}
		for (int y = 2; y < height - 1; y+=2) {
			ret.put(getZIndex(0, y, 1), createSelection(null, UI_DOT_L, 0, y));
			ret.put(getZIndex(width - 1, y, 1), createSelection(null, UI_DOT_R, width - 1, y));
		}
		
		return ret;
	}
	
	private long getScreenTile(int tile, int y) {
		if (DotsModel.MARK == tile) {
			if (y % 2 == 0) {
				return UI_H_MARK;
			} else {
				return UI_V_MARK;
			}
		} else {
			return tile;
		}
	}

	@Override
	public int getZIndex(int x, int y, int z) {
		int w = getWidth() << 1;
		int h = getHeight() << 1;
		return (x << 1) + (y << 1) * w + z * w * h;
	}
	
	@Override
	public void doStartTurn() {
		super.doStartTurn();
		getModel().setMoved(false);
	}

	@Override
	protected boolean isMove() {
		boolean ret = false;
		DotsModel game = getModel();
		List<GameSelection> selections = game.getSelections();
		ret = selections.size() > 0;
//		if (ret) {
//			DotsSelection selection = (DotsSelection) selections.get(selections.size() - 1);
//			ret = !selection.isClose();
//		}
		return ret;
	}

	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		super.onInitResourceProvider(resourceProvider);

		resourceProvider.addResource(null, UI_EMPTY);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/dot.svg", UI_DOT);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/v_mark.svg", UI_V_MARK);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/h_mark.svg", UI_H_MARK);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/red_box.svg", UI_RED); 
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/blue_box.svg", UI_BLUE);

		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/tl_dot.svg", UI_DOT_TL);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/tr_dot.svg", UI_DOT_TR);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/bl_dot.svg", UI_DOT_BL);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/br_dot.svg", UI_DOT_BR);
		

		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/t_dot.svg", UI_DOT_T);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/r_dot.svg", UI_DOT_R);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/b_dot.svg", UI_DOT_B);
		resourceProvider.addResource("com/pigdroid/game/board/tile/dots/l_dot.svg", UI_DOT_L);

	}

	@Override
	public void select(GameSelection... gameSelections) {
		super.select(gameSelections);
	}

	@Override
	public boolean isWinner(Player currentPlayer) {
		DotsModel model = getModel();
//		int playerIndex = model.getCurrentTurn() + 1;
		IntTileLayer layer = model.getLayer(1);
		boolean ret = false;
		if (DotsHelper.canMove(layer)) {
			ret  = false;
		} else {
			ret = isMaxCount(model, layer);
		}
		return ret;
	}
	
	@Override
	public boolean isLoser(Player currentPlayer) {
		DotsModel model = getModel();
//		int playerIndex = model.getCurrentTurn() + 1;
		IntTileLayer layer = model.getLayer(1);
		boolean ret = false;
		if (DotsHelper.canMove(layer)) {
			ret  = false;
		} else {
			ret = !isMaxCount(model, layer);
		}
		return ret;
	}
	
	@Override
	public boolean isTie(Player currentPlayer) {
		DotsModel model = getModel();
		boolean ret = isSameCount(model, model.getLayer(1)) && !DotsHelper.canMove(model.getLayer(1));
		return ret;
	}

	private boolean isSameCount(DotsModel model, IntTileLayer layer) {
		boolean ret = true;
		int maxPlayer = model.getPlayerCount();
		int count = DotsHelper.count(layer, 0);
		for (int playerIndex = 1; playerIndex < maxPlayer; playerIndex++) {
			int currentCount = DotsHelper.count(layer, playerIndex);
			if (currentCount != count) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	private boolean isMaxCount(DotsModel model, IntTileLayer layer) {
		// Count for each player and see if current wins
		int maxPlayer = model.getPlayerCount();
		int maxCount = 0; 
		int maxCountIndex = -1;
		for (int playerIndex = 0; playerIndex < maxPlayer; playerIndex++) {
			int count = DotsHelper.count(layer, playerIndex);
			if (count > maxCount) {
				maxCount = count;
				maxCountIndex = playerIndex;
			}
		}
		boolean ret = false;
		if (maxCountIndex > -1) {
			ret = maxCountIndex == model.getCurrentTurn();
		}
		return ret;
	}

	@Override
	public boolean isNeverReadyToStart() {
		DotsModel model = getModel();
		int left = 0;
		for (Player player : model.getPlayers()) {
			if (PlayerStatusEnum.GONE.equals(player.getStatus())) {
				left ++;
			}
		}
		return left > 0;
	}
	
	@Override
	protected UIGameContext createUIContext() {
		UIGameContext ctx = super.createUIContext();
		ctx.setRotationDegreesPerPlayer(0);
		return ctx;
	}
	
}
