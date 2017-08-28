package com.pigdroid.game.board.tile.dots.controller;

import java.util.ArrayList;
import java.util.List;

import com.pigdroid.game.board.tile.dots.model.DotsSelection;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;

public class DotsHelper {

	public static final int EMPTY		= 0;
	public static final int DOT			= 1;
	public static final int MARK		= 2;

	private DotsHelper() {
		
	}
	
	public static IntTileLayer getInitialLayer(IntTileLayer layer, int w, int h) {
		int width = w;
		int height = h;
		if (layer == null) {
			layer = new IntTileLayer(width, height);
		} else {
			layer.setWidth(width);
			layer.setHeight(height);
		}
		layer.clear(EMPTY);
		for (int x = 0; x < width; x += 2) {
			for (int y = 0; y < height; y += 2) {
				layer.set(x, y, DOT);
			}
		}
//		for (int y = 0; y < height; y ++) {
//			for (int x = 0; x < width; x ++) {
//				System.out.print(layer.get(x, y));
//			}
//			System.out.println();
//		}
		return layer;
	}
	
	public static boolean closeBoxes(IntTileLayer layer, int playerIndex) {
		int width = layer.getWidth();
		int height = layer.getHeight();
		boolean closed = false;
		for (int x = 1; x < width; x += 2) {
			for (int y = 1; y < height; y += 2) {
				if (layer.get(x, y) == EMPTY
						&& isClosedBox(x, y, layer)) {
					layer.set(x, y, getPlayerTile(playerIndex));
					closed = true;
				}
			}
		}
		return closed;
	}

	private static Integer getPlayerTile(int playerIndex) {
		return MARK + playerIndex + 1;
	}

	private static boolean isClosedBox(int x, int y, IntTileLayer layer) {
		if (x % 2 == 0) {
			return false;
		}
		if (y % 2 == 0) {
			return false;
		}
		if (layer.get(x - 1, y) == MARK
				&& layer.get(x + 1, y) == MARK
				&& layer.get(x, y - 1) == MARK
				&& layer.get(x, y + 1) == MARK) {
			return true;
		}
		return false;
	}
	
	public static boolean canMove(IntTileLayer layer) {
		int width = layer.getWidth();
		int height = layer.getHeight();
		for (int x = 1; x < width; x += 2) {
			for (int y = 1; y < height; y += 2) {
				if (!isClosedBox(x, y, layer)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static List<TileBoardGameSelection> getSelectables(IntTileLayer layer) {
		int width = layer.getWidth();
		int height = layer.getHeight();
		List<TileBoardGameSelection> ret = new ArrayList<TileBoardGameSelection>();
		// If there´s one to close they´re prioritary. Player must close the box!
		getOneToCloseSelectables(layer, width, height, ret);
		if (ret.isEmpty()) {
			getOtherSelectables(layer, width, height, ret);
		}
		return ret;
	}

	private static void getOneToCloseSelectables(IntTileLayer layer, int width,
			int height, List<TileBoardGameSelection> ret) {
		for (int x = 1; x < width; x += 2) {
			for (int y = 1; y < height; y += 2) {
				if (layer.get(x, y) == EMPTY) {
					getOneToCloseSelectable(layer, x, y, x-1, y, ret);
					getOneToCloseSelectable(layer, x, y, x+1, y, ret);
					getOneToCloseSelectable(layer, x, y, x, y-1, ret);
					getOneToCloseSelectable(layer, x, y, x, y+1, ret);
				}
			}
		}
		
	}

	private static void getOneToCloseSelectable(IntTileLayer layer, int x,
			int y, int xdest, int ydest, List<TileBoardGameSelection> ret) {
		Integer val = layer.get(xdest, ydest);
		if (val == EMPTY) {
			layer.set(xdest, ydest, MARK);
			if (isClosedBox(x, y, layer)) {
				DotsSelection selection = new DotsSelection();
				selection.setX(xdest);
				selection.setY(ydest);
				ret.add(selection);
			}
			layer.set(xdest, ydest, val);
		}		
	}

	private static void getOtherSelectables(IntTileLayer layer, int width,
			int height, List<TileBoardGameSelection> ret) {
		for (int x = 1; x < width; x += 2) {
			for (int y = 1; y < height; y += 2) {
				if (layer.get(x, y) == EMPTY) {
					if (layer.get(x - 1, y) == EMPTY) {
						DotsSelection selection = new DotsSelection();
						selection.setX(x - 1);
						selection.setY(y);
						ret.add(selection);
					}
					if (layer.get(x + 1, y) == EMPTY) {
						DotsSelection selection = new DotsSelection();
						selection.setX(x + 1);
						selection.setY(y);
						ret.add(selection);
					}
					if (layer.get(x, y - 1) == EMPTY) {
						DotsSelection selection = new DotsSelection();
						selection.setX(x);
						selection.setY(y - 1);
						ret.add(selection);
					}
					if (layer.get(x, y + 1) == EMPTY) {
						DotsSelection selection = new DotsSelection();
						selection.setX(x);
						selection.setY(y + 1);
						ret.add(selection);
					}
				}
			}
		}
	}
	
	public static int count(IntTileLayer layer, int playerIndex) {
		int width = layer.getWidth();
		int height = layer.getHeight();
		int ret = 0;
		for (int x = 1; x < width; x += 2) {
			for (int y = 1; y < height; y += 2) {
				if (isPlayerTile(layer.get(x, y), playerIndex)) {
					ret ++;
				}
			}
		}
		return ret;
	}

	private static boolean isPlayerTile(int tile, int playerIndex) {
		return tile == getPlayerTile(playerIndex);
	}
	
}
