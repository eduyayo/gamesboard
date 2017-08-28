package com.pigdroid.game.board.tile.dots.controller;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.dots.model.DotsModel;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.UIGameContext;

public class DotsControllerTest {

	private DotsController controller;
	private UITileBoardGameContext graphicContext;
	private DotsModel model;
	private boolean gameEnded = false;
	
	private boolean winner = false;
	private boolean loser = false;
	private boolean tie = false;
	
	@Before
	public void setup() {
		controller = new DotsController() {
			@Override
			protected void onInitModel(DotsModel game) {
				super.onInitModel(game);
				model = game;
			}
		};
		controller.newGame();
	}

	@Test
	public void testInitLayer() {
		DotsHelper.getInitialLayer(null, 3, 3);
	}

//	@Test 
//	public void testStartGame() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		GameControllerListener gameListener = createMock(GameControllerListener.class);
//		controller.setGameControllerListener(gameListener);
//		
//		GameUIControllerListener uiListener = createMock(GameUIControllerListener.class);
//		controller.setGameUIControllerListener(uiListener);
//		
//		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
//		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
//		
//		//Record listener
//		gameListener.onPlayerJoined(eduyayo);
//		uiListener.onGamePaint((UIGameContext) anyObject());
//		gameListener.onPlayerJoined(pigdroid);
//		uiListener.onGamePaint((UIGameContext) anyObject());
//		gameListener.onStartGame();
//
//		replay(gameListener, uiListener);
//		controller.addPlayer(eduyayo);
//		controller.addPlayer(pigdroid);
//		controller.joinPlayer("eduyayo@gmail.com");
//		controller.joinPlayer("pigdroid@gmail.com");
//		verify(gameListener);
//		verify(uiListener);
//		
//		Field field = GameController.class.getDeclaredField("model");
//		field.setAccessible(true);
//		DotsModel model = (DotsModel) field.get(controller);
//		Assert.assertNotNull(model);
//		Assert.assertEquals(eduyayo, model.getCurrentPlayer());
//		Assert.assertNotNull(model.getCurrentPlayer());
//		
//		Method getSelectables = DotsController.class.getDeclaredMethod("getSelectables");
//		getSelectables.setAccessible(true);
//		Object value = getSelectables.invoke(controller);
//		Assert.assertNotNull(value);
//		Map<Integer, Object> collection = (Map<Integer, Object>) value;
//		Assert.assertFalse(collection.isEmpty());
//	}
	

//	@Test
//	public void testFirstMove() {
//		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
//			@Override
//			public void onGamePaint(UIGameContext graphicContext) {
//				DotsControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
//			}
//		});
//		
//		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
//		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
//		controller.addPlayer(eduyayo);
//		controller.addPlayer(pigdroid);
//		controller.joinPlayer("eduyayo@gmail.com");
//		controller.joinPlayer("pigdroid@gmail.com");
//
//		Iterator<Integer> it = graphicContext.getSelectables().keySet().iterator();
//		it.next();
//		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext.getSelectables().get(it.next());
//		int x = selection.getX();
//		int y = selection.getY();
//		controller.select(selection);
//		boolean foundTopLeft = false;
//		boolean foundTopRight = false;
////		int count = 0;
////		for (int key : graphicContext.getSelectables().keySet()) {
////			selection = (TileBoardGameSelection) graphicContext.getSelectables().get(key);
////			count ++;
////			if (selection.getX() == x - 1 && selection.getY() == y - 1) {
////				foundTopLeft = true;
////			}
////			if (selection.getX() == x + 1 && selection.getY() == y - 1) {
////				foundTopRight = true;
////			}
////		}
////		Assert.assertTrue(foundTopLeft);
////		Assert.assertTrue(foundTopRight);
////		Assert.assertEquals(2, count);
//	}
	
	
	
	
//	@Test
//	public void testFullGame() {
//
//		controller
//				.setGameUIControllerListener(new GameControllerListenerAdapter() {
//					@Override
//					public void onGamePaint(UIGameContext graphicContext) {
//						DotsControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
//					}
//				});
//		controller.setGameControllerListener( new GameControllerListenerAdapter() {
//
//			@Override
//			public void onEndGame(Player currentPlayer, boolean pwinner, boolean ploser, boolean ptie) {
//				super.onEndGame(currentPlayer, pwinner, ploser, ptie);
//				winner = pwinner; 
//				loser = ploser; 
//				tie = ptie;
//				gameEnded = true;
//			}
//			
//		});
//
//		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
//		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
//		controller.addPlayer(eduyayo);
//		controller.addPlayer(pigdroid);
//		controller.joinPlayer("eduyayo@gmail.com");
//		controller.joinPlayer("pigdroid@gmail.com");
//
//		int maxRoundCount = 300;
//		
//		while (maxRoundCount > 0) {
//			if (graphicContext.getSelectables().keySet().isEmpty()) {
//				break;
//			}
//			Iterator<Integer> it = graphicContext.getSelectables().keySet().iterator();
//			Integer key = it.next();
//			if (it.hasNext()) {
//				key = it.next();
//			}
//			TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext.getSelectables().get(key);
//			controller.select(selection);
//			maxRoundCount --;
//			
//		}
//		Assert.assertTrue(maxRoundCount > 0);
//		Assert.assertTrue(gameEnded);
//
//		Assert.assertFalse(winner); 
//		Assert.assertTrue(loser); 
//		Assert.assertFalse(tie);
//	}
	
	@Test
	public void testRender() {
		controller.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				Map<Integer, GameSelection> elements = graphicContext.getUiElements();
				for (int i = 0; i < 2; i++) {
					for (int y = 0; y < controller.getHeight(); y++) {
						for (int x = 0; x < controller.getWidth(); x++) {
							GameSelection element = elements.get(controller.getZIndex(x, y, i));
							if (element != null) {
								System.out.print(element.getModelId());	
							} else {
								System.out.print(0);	
							}
						}	
						System.out.println();
					}
					System.out.println();
				}
				
			}
		});
		controller.newGame();
		controller.doForcePaint();
	}
	
}
