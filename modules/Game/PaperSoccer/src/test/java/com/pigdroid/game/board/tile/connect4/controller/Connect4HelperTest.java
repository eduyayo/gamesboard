package com.pigdroid.game.board.tile.connect4.controller;

import junit.framework.Assert;

import org.junit.Test;

import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.papersoccer.controller.PaperSoccerHelper;

public class Connect4HelperTest {
	

	@Test
	public void testIsWinner() {
		IntTileLayer layer = new IntTileLayer(5, 5);
		layer.clear(PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 1));

		layer.set(0, 0, PaperSoccerHelper.RED);
		layer.set(0, 1, PaperSoccerHelper.RED);
		layer.set(0, 2, PaperSoccerHelper.RED);
		layer.set(0, 3, PaperSoccerHelper.RED);
		Assert.assertTrue(PaperSoccerHelper.isWinner(layer, 0));
		layer.set(0, 3, PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		layer.clear(PaperSoccerHelper.BLANK);
		
		layer.set(0, 0, PaperSoccerHelper.RED);
		layer.set(1, 0, PaperSoccerHelper.RED);
		layer.set(2, 0, PaperSoccerHelper.RED);
		layer.set(3, 0, PaperSoccerHelper.RED);
		Assert.assertTrue(PaperSoccerHelper.isWinner(layer, 0));
		layer.set(3, 0, PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		layer.clear(PaperSoccerHelper.BLANK);

		layer.set(0, 0, PaperSoccerHelper.RED);
		layer.set(1, 1, PaperSoccerHelper.RED);
		layer.set(2, 2, PaperSoccerHelper.RED);
		layer.set(3, 3, PaperSoccerHelper.RED);
		Assert.assertTrue(PaperSoccerHelper.isWinner(layer, 0));
		layer.set(3, 3, PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		layer.clear(PaperSoccerHelper.BLANK);
		

		layer.set(3, 0, PaperSoccerHelper.RED);
		layer.set(2, 1, PaperSoccerHelper.RED);
		layer.set(1, 2, PaperSoccerHelper.RED);
		layer.set(0, 3, PaperSoccerHelper.RED);
		Assert.assertTrue(PaperSoccerHelper.isWinner(layer, 0));
		layer.set(3, 0, PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		layer.clear(PaperSoccerHelper.BLANK);
		

		layer = new IntTileLayer(7, 6);
		layer.clear(PaperSoccerHelper.BLANK);
		layer.set(0, 5, PaperSoccerHelper.RED);
		layer.set(1, 5, PaperSoccerHelper.RED);
		layer.set(2, 5, PaperSoccerHelper.RED);
		layer.set(3, 5, PaperSoccerHelper.RED);
		Assert.assertTrue(PaperSoccerHelper.isWinner(layer, 0));
		layer.set(0, 5, PaperSoccerHelper.BLANK);
		Assert.assertFalse(PaperSoccerHelper.isWinner(layer, 0));
		layer.clear(PaperSoccerHelper.BLANK);
		
	}
	
}
