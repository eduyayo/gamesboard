package com.pigdroid.game.board.tile.dots.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.model.IntTileLayer;

public class DotsHelperTest {

	private IntTileLayer layer = null;
	
	@Before
	public void setup() {
		layer = DotsHelper.getInitialLayer(layer, 3, 3);
	}
	
	@Test
	public void testCanMove() {
		Assert.assertTrue(DotsHelper.canMove(layer));
		layer.set(1, 0, DotsHelper.MARK);
		Assert.assertTrue(DotsHelper.canMove(layer));
		layer.set(2, 1, DotsHelper.MARK);
		Assert.assertTrue(DotsHelper.canMove(layer));
		layer.set(0, 1, DotsHelper.MARK);
		Assert.assertTrue(DotsHelper.canMove(layer));
		layer.set(1, 2, DotsHelper.MARK);
		Assert.assertFalse(DotsHelper.canMove(layer));
	}
	
	@Test
	public void testCloseBoxes() {
		layer = DotsHelper.getInitialLayer(layer, 2, 2);
		layer.set(1, 0, DotsHelper.MARK);
		layer.set(2, 1, DotsHelper.MARK);
		layer.set(0, 1, DotsHelper.MARK);
		layer.set(1, 2, DotsHelper.MARK);
		Assert.assertEquals(DotsHelper.EMPTY, (int)layer.get(1, 1));
		DotsHelper.closeBoxes(layer, 0);
		Assert.assertEquals(DotsHelper.MARK + 1, (int)layer.get(1, 1));
	}

	@Test
	public void testCount() {
		Assert.assertEquals(0, DotsHelper.count(layer, 0));
		Assert.assertEquals(0, DotsHelper.count(layer, 1));
		testCloseBoxes();
		Assert.assertEquals(1, DotsHelper.count(layer, 0));
		Assert.assertEquals(0, DotsHelper.count(layer, 1));
	}
	
	@Test
	public void testGetInitialLayer() {
		DotsHelper.getInitialLayer(null, 1, 1);
		DotsHelper.getInitialLayer(layer, 3, 3);
	}
	
	@Test
	public void testGetSelectables() {
		Assert.assertEquals(4, DotsHelper.getSelectables(layer).size());
		layer.set(1, 0, DotsHelper.MARK);
		Assert.assertEquals(3, DotsHelper.getSelectables(layer).size());
		layer.set(2, 1, DotsHelper.MARK);
		Assert.assertEquals(2, DotsHelper.getSelectables(layer).size());
		layer.set(0, 1, DotsHelper.MARK);
		Assert.assertEquals(1, DotsHelper.getSelectables(layer).size());
		layer.set(1, 2, DotsHelper.MARK);
		Assert.assertEquals(0, DotsHelper.getSelectables(layer).size());
		DotsHelper.closeBoxes(layer, 1);
		Assert.assertEquals(0, DotsHelper.getSelectables(layer).size());
	}
	
}
