package com.pigdroid.game.board.tile.connect4.controller;

import junit.framework.Assert;

import org.junit.Test;

import com.pigdroid.game.board.tile.model.IntTileLayer;

public class Connect4HelperTest {
	

	@Test
	public void testIsWinner() {
		IntTileLayer layer = new IntTileLayer(5, 5);
		layer.clear(Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		Assert.assertFalse(Connect4Helper.isWinner(layer, 1));

		layer.set(0, 0, Connect4Helper.RED);
		layer.set(0, 1, Connect4Helper.RED);
		layer.set(0, 2, Connect4Helper.RED);
		layer.set(0, 3, Connect4Helper.RED);
		Assert.assertTrue(Connect4Helper.isWinner(layer, 0));
		layer.set(0, 3, Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		layer.clear(Connect4Helper.BLANK);
		
		layer.set(0, 0, Connect4Helper.RED);
		layer.set(1, 0, Connect4Helper.RED);
		layer.set(2, 0, Connect4Helper.RED);
		layer.set(3, 0, Connect4Helper.RED);
		Assert.assertTrue(Connect4Helper.isWinner(layer, 0));
		layer.set(3, 0, Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		layer.clear(Connect4Helper.BLANK);

		layer.set(0, 0, Connect4Helper.RED);
		layer.set(1, 1, Connect4Helper.RED);
		layer.set(2, 2, Connect4Helper.RED);
		layer.set(3, 3, Connect4Helper.RED);
		Assert.assertTrue(Connect4Helper.isWinner(layer, 0));
		layer.set(3, 3, Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		layer.clear(Connect4Helper.BLANK);
		

		layer.set(3, 0, Connect4Helper.RED);
		layer.set(2, 1, Connect4Helper.RED);
		layer.set(1, 2, Connect4Helper.RED);
		layer.set(0, 3, Connect4Helper.RED);
		Assert.assertTrue(Connect4Helper.isWinner(layer, 0));
		layer.set(3, 0, Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		layer.clear(Connect4Helper.BLANK);
		

		layer = new IntTileLayer(7, 6);
		layer.clear(Connect4Helper.BLANK);
		layer.set(0, 5, Connect4Helper.RED);
		layer.set(1, 5, Connect4Helper.RED);
		layer.set(2, 5, Connect4Helper.RED);
		layer.set(3, 5, Connect4Helper.RED);
		Assert.assertTrue(Connect4Helper.isWinner(layer, 0));
		layer.set(0, 5, Connect4Helper.BLANK);
		Assert.assertFalse(Connect4Helper.isWinner(layer, 0));
		layer.clear(Connect4Helper.BLANK);
		
	}
	
}
