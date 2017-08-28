package com.pigdroid.game.resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResourceProviderTest {
	
	private ResourceProvider fixture;
	private Integer idWhite;
	private Integer idBlack;

	@Before
	public void setup() {
		this.fixture = new ResourceProvider() {
			{
				idWhite = addResource("com/pigdroid/game/checkers/board/black_tile.svg");
				idBlack = addResource("com/pigdroid/game/checkers/board/white_tile.svg");
			}
		};
	}
	
	@After
	public void tearDown() {
		fixture = null;
	}

	@Test
	public void testGetResourceByName() {
		Assert.assertNotNull(fixture.getResource("com/pigdroid/game/checkers/board/black_tile.svg"));
		Assert.assertNotNull(fixture.getResource("com/pigdroid/game/checkers/board/white_tile.svg"));
	}

	@Test
	public void testGetResourceById() {
		Assert.assertNotNull(fixture.getResource(idBlack));
		Assert.assertNotNull(fixture.getResource(idWhite));
	}

}
