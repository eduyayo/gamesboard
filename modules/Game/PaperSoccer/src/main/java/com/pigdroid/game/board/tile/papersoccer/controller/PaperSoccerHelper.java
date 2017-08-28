package com.pigdroid.game.board.tile.papersoccer.controller;

import java.util.ArrayList;
import java.util.List;

import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.papersoccer.model.PaperSoccerSelection;

public class PaperSoccerHelper {

	public static final int BLANK	= 0;
	public static final int RED		= 1;
	public static final int BLUE	= 2;
	
	private PaperSoccerHelper() {
	}
	
	public static final boolean isCorner(int mapTile) {
		return mapTile != 0;
	}
	
	public static final boolean isTile(int mapTile, int tile) {
		//TODO
		return mapTile != 0;
	}
	
	
	
	
	
	
	
	public static IntTileLayer getInitialLayer(IntTileLayer layer) {
		layer = layer != null ? layer : new IntTileLayer(7, 6);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				layer.set(i, j, BLANK);
			}
		}
		
		return layer;
	}

	public static boolean isWinner(IntTileLayer layer, int playerIndex) {
		return false;
	}

	public static List<PaperSoccerSelection> getCanMoveTos(IntTileLayer layer) {
		List<PaperSoccerSelection> ret = new ArrayList<PaperSoccerSelection>();
		int width = layer.getWidth();
		for (int x = 0; x < width; x++) {
			if (BLANK == layer.get(x, 0)) {
				ret.add(createSelection(x, 0));
			}
		}
		return ret;
	}

	private static PaperSoccerSelection createSelection(int x, int y) {
		PaperSoccerSelection ret = new PaperSoccerSelection();
		ret.setX(x);
		ret.setY(y);
		ret.setLayer(1);
		return ret ;
	}

	public static void move(IntTileLayer layer, PaperSoccerSelection gameSelection, int playerIndex) {
		int x = gameSelection.getX();
		int y = layer.getHeight() - 1;
		while (layer.get(x, y) != BLANK) {
			y--;
		}
		layer.set(x,  y,  playerIndex + RED);
	}
	
}
