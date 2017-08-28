package com.pigdroid.game.board.tile.chess.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.model.IntTileLayer;

public class ChessHelperTest {
	
	private IntTileLayer layer;

	@Before
	public void setup() {
		layer = ChessHelper.getInitialLayer();
	}

	@Test
	public void testInitNotFail() {
		Assert.assertNotNull(layer);
	}

	@Test
	public void testIsPiece() {
		Assert.assertTrue(ChessHelper.isPawn('p'));
		Assert.assertTrue(ChessHelper.isTower('t'));
		Assert.assertTrue(ChessHelper.isKnight('h'));
		Assert.assertTrue(ChessHelper.isBishop('b'));
		Assert.assertTrue(ChessHelper.isKing('k'));
		Assert.assertTrue(ChessHelper.isQueen('q'));
		Assert.assertTrue(ChessHelper.isPawn('P'));
		Assert.assertTrue(ChessHelper.isTower('T'));
		Assert.assertTrue(ChessHelper.isKnight('H'));
		Assert.assertTrue(ChessHelper.isBishop('B'));
		Assert.assertTrue(ChessHelper.isKing('K'));
		Assert.assertTrue(ChessHelper.isQueen('Q'));

		Assert.assertTrue(ChessHelper.isEnemies('p', 'P'));
		Assert.assertTrue(ChessHelper.isEnemies('P', 'p'));
		Assert.assertFalse(ChessHelper.isEnemies('P', 'P'));
	}

	@Test
	public void testCanMove() {
		Assert.assertFalse(ChessHelper.getCanMoves(layer, true).isEmpty());
		Assert.assertEquals(10, ChessHelper.getCanMoves(layer, true).size());
		Assert.assertFalse(ChessHelper.getCanMoves(layer, false).isEmpty());
		Assert.assertEquals(10, ChessHelper.getCanMoves(layer, false).size());
		
		for (int x = 0; x < 8; x++) {
			//pawns can move
			Assert.assertTrue(ChessHelper.canMove(x, 1, layer));
			Assert.assertEquals(2, ChessHelper.getCanMoveTos(x, 1, layer, true).size());
			Assert.assertTrue(ChessHelper.canMove(x, 6, layer));
			Assert.assertEquals(2, ChessHelper.getCanMoveTos(x, 6, layer, true).size());
			if (x != 1 && x != 6) {
				//none of these can
				Assert.assertFalse(ChessHelper.canMove(x, 0, layer));
				Assert.assertEquals(0, ChessHelper.getCanMoveTos(x, 0, layer, true).size());
				Assert.assertFalse(ChessHelper.canMove(x, 7, layer));
				Assert.assertEquals(0, ChessHelper.getCanMoveTos(x, 7, layer, true).size());
			} else { 
				//knights move
				Assert.assertTrue(ChessHelper.canMove(x, 0, layer));
				Assert.assertEquals(2, ChessHelper.getCanMoveTos(x, 0, layer, true).size());
				Assert.assertTrue(ChessHelper.canMove(x, 7, layer));
				Assert.assertEquals(2, ChessHelper.getCanMoveTos(x, 7, layer, true).size());
			}
		}
	}
	
	@Test
	public void testKingMove() {
		layer = getEmpty(null);
		layer.set(0, 0, ChessHelper.KING);
		Assert.assertEquals(1, ChessHelper.getCanMoves(layer, false).size());
		Assert.assertEquals(3, ChessHelper.getCanMoveTos(0, 0, layer, true).size());

		layer = getEmpty(null);
		layer.set(1, 0, ChessHelper.KING);
		Assert.assertEquals(1, ChessHelper.getCanMoves(layer, false).size());
		Assert.assertEquals(5, ChessHelper.getCanMoveTos(1, 0, layer, true).size());

		layer = getEmpty(null);
		layer.set(1, 1, ChessHelper.KING);
		Assert.assertEquals(1, ChessHelper.getCanMoves(layer, false).size());
		Assert.assertEquals(8, ChessHelper.getCanMoveTos(1, 1, layer, true).size());
	}

	private IntTileLayer getEmpty(IntTileLayer l) {
		l = l != null ? l : new IntTileLayer(8, 8);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				l.set(i, j, ChessHelper.EMPTY);
			}
		}
		return l;
	}

	private IntTileLayer getKingLayer(IntTileLayer l) {
		l = l != null ? l : new IntTileLayer(8, 8);
		l.set(3, 7, Character.toLowerCase(ChessHelper.KING));
		l.set(4, 0, Character.toUpperCase(ChessHelper.KING));
		return l;
	}

	private IntTileLayer getQueenLayer(IntTileLayer l) {
		l = l != null ? l : new IntTileLayer(8, 8);
		l.set(4, 7, Character.toLowerCase(ChessHelper.QUEEN));
		l.set(3, 0, Character.toUpperCase(ChessHelper.QUEEN));
		return layer;
	}

	private IntTileLayer getTowerLayer(IntTileLayer l) {
		l = l != null ? l : new IntTileLayer(8, 8);
		l.set(0, 7, Character.toLowerCase(ChessHelper.TOWER));
		l.set(7, 7, Character.toLowerCase(ChessHelper.TOWER));
		l.set(0, 0, Character.toUpperCase(ChessHelper.TOWER));
		l.set(7, 0, Character.toUpperCase(ChessHelper.TOWER));
		return layer;
	}
	
	@Test
	public void testCheck() {
		layer = getEmpty(null);
		layer = getKingLayer(layer);
		Assert.assertEquals(ChessHelper.CHECK_LEVEL_NONE, ChessHelper.getCheckLevel(layer, true, null));
		Assert.assertEquals(ChessHelper.CHECK_LEVEL_NONE, ChessHelper.getCheckLevel(layer, false, null));
		
		layer = getQueenLayer(layer);
		Assert.assertEquals(ChessHelper.CHECK_LEVEL_CHECK, ChessHelper.getCheckLevel(layer, true, null));
		Assert.assertEquals(ChessHelper.CHECK_LEVEL_CHECK, ChessHelper.getCheckLevel(layer, false, null));

		layer.set(3, 7, Character.toLowerCase(ChessHelper.QUEEN));
		layer.set(4, 7, Character.toLowerCase(ChessHelper.QUEEN));
		layer.set(5, 7, Character.toLowerCase(ChessHelper.QUEEN));
		Assert.assertEquals(ChessHelper.CHECK_LEVEL_MATE, ChessHelper.getCheckLevel(layer, false, null));
	}
	
	
	@Test
	public void testOO() {
		layer = getEmpty(null);
		layer = getKingLayer(layer);
		layer = getTowerLayer(layer);
		
//		throw new RuntimeException("Not implemented");
		
		/*
		 * 
		 * 	init	thbqkbht
		 * 			t---k--t
		 * 	o-o		t----tk-
		 * 	o-o-o	--kt----
		 * 
		 * 
		 */
		
	}
	
}
