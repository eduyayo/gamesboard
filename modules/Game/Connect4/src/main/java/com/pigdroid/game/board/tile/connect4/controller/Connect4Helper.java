package com.pigdroid.game.board.tile.connect4.controller;

import java.util.ArrayList;
import java.util.List;

import com.pigdroid.game.board.tile.connect4.model.Connect4Selection;
import com.pigdroid.game.board.tile.model.IntTileLayer;

public class Connect4Helper {

	public static final int BLANK	= 0;
	public static final int RED		= 1;
	public static final int BLUE	= 2;
	
	private Connect4Helper() {
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
		final int tileToFind = playerIndex + RED;
		final int[] count = {0};
		final int[] previousColor = {123};
		final boolean [] ret = {false};
		ArrayRunner.run(layer, new ArrayRunner.Visitor<Integer>() {

			public void visit(int x, int y, Integer element) {
				if (tileToFind == element) {
					count[0] ++;
					if (count[0] == 3) {
						ret[0] = true;
					}
				}
				if (previousColor[0] != element) {
					count[0] = 0;
				}
				previousColor[0] = element;
			}

		}, new ArrayRunner.LineVisitor() {
			
			public void visit() {
				count[0] = 0;
				previousColor[0] = 123;
			}
			
		});
		return ret[0];
	}

	public static List<Connect4Selection> getCanMoveTos(IntTileLayer layer) {
		List<Connect4Selection> ret = new ArrayList<Connect4Selection>();
		int width = layer.getWidth();
		for (int x = 0; x < width; x++) {
			if (BLANK == layer.get(x, 0)) {
				ret.add(createSelection(x, 0));
			}
		}
		return ret;
	}

	private static Connect4Selection createSelection(int x, int y) {
		Connect4Selection ret = new Connect4Selection();
		ret.setX(x);
		ret.setY(y);
		ret.setLayer(1);
		return ret ;
	}

	public static void move(IntTileLayer layer, Connect4Selection gameSelection, int playerIndex) {
		int x = gameSelection.getX();
		int y = layer.getHeight() - 1;
		while (layer.get(x, y) != BLANK) {
			y--;
		}
		layer.set(x,  y,  playerIndex + RED);
	}
	
}
