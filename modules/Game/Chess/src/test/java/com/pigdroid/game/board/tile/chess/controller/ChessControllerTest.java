package com.pigdroid.game.board.tile.chess.controller;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.chess.model.ChessModel;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameControllerListenerAdapter;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.model.memento.Memento;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;
import com.pigdroid.util.Base64;

public class ChessControllerTest {

	private ChessController controller;
	private UITileBoardGameContext graphicContext;
	private ChessModel model;
	private boolean gameEnded = false;
	
	@Before
	public void setup() {
		controller = new ChessController();
		controller.newGame();
	}

//	@Test
//	public void testInitNotFail() {
//		controller.doTick(model.getCurrentPlayer());
//	}
	
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
//		ChessModel model = (ChessModel) field.get(controller);
//		Assert.assertNotNull(model);
//		Assert.assertEquals(eduyayo, model.getCurrentPlayer());
//		Assert.assertNotNull(model.getCurrentPlayer());
//		
//		Method getSelectables = ChessController.class.getDeclaredMethod("getSelectables");
//		getSelectables.setAccessible(true);
//		Object value = getSelectables.invoke(controller);
//		Assert.assertNotNull(value);
//
//	}

	@Test
	public void testFirstMove() {
		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
			@Override
			public void onGamePaint(UIGameContext graphicContext) {
				ChessControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
			}
		});
		
		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		Iterator<Integer> it = graphicContext.getSelectables().keySet().iterator();
		Assert.assertEquals(0, graphicContext.getSelectables().size());
//		it.next();
//		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext.getSelectables().get(it.next());
//		int x = selection.getX();
//		int y = selection.getY();
//		controller.select(selection);
//		boolean foundTopLeft = false;
//		boolean foundTopRight = false;
//		int count = 0;
//		Assert.assertEquals(2, graphicContext.getSelectables().size());
	}
	
//	@Test
//	public void testDoubleJump() {
//		controller = new ChessController() {
//			@Override
//			protected void onInitModel(ChessModel game) {
//				super.onInitModel(game);
//				model = game;
//				IntTileLayer layer = game.getLayer(1);
//				layer.clear(ChessModel.BLANK);
//				layer.set(0, 7, ChessModel.PAWN_WHITE);
//				layer.set(1, 6, ChessModel.PAWN_BLACK);
//				layer.set(3, 4, ChessModel.PAWN_BLACK);
//			}
//		};
//		
//		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
//			@Override
//			public void onGamePaint(UIGameContext graphicContext) {
//				ChessControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
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
//		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext.getSelectables().get(it.next());
//		controller.select(selection);
//		Assert.assertEquals(1, graphicContext.getSelectables().size());
//		Assert.assertEquals(2, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getX());
//		Assert.assertEquals(5, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getY());
//		Assert.assertEquals(1, graphicContext.getSelections().size());
//		Assert.assertEquals(0, (int)model.getCurrentTurn());
//		controller.select(graphicContext.getSelectables().entrySet().iterator().next().getValue());
//		Assert.assertEquals(1, graphicContext.getSelectables().size());
//		Assert.assertEquals(0, (int)model.getCurrentTurn());
//		Assert.assertEquals(4, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getX());
//		Assert.assertEquals(3, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getY());
//		Assert.assertEquals(1, graphicContext.getSelections().size());
//	}

//	@Test
//	public void testUndoCommit() {
//
//		controller = new ChessController() {
//			@Override
//			protected void onInitModel(ChessModel game) {
//				super.onInitModel(game);
//				model = game;
//			}
//		};
//		
//		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
//			@Override
//			public void onGamePaint(UIGameContext graphicContext) {
//				ChessControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
//			}
//		});
//		
//		controller.setTurnGameControllerListener(new TurnGameControllerListener() {
//
//			public void onStartTurn(Player player) {
//			}
//
//			public void onEndTurn(Player player) {
//			}
//
//		});
//
//		model.setDirty(true);
//		String control1 = controller.getSerializedModel();
//		
//		controller.checkpoint();
//		model.setDirty(true);
//		Assert.assertEquals(control1, controller.getSerializedModel());
//		controller.rollback();
//		model.setDirty(true);
//		Assert.assertEquals(byteArrayToSortedList(getSave(control1)), byteArrayToSortedList(getSave(controller.getSerializedModel())));
//		
//		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
//		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
//		controller.addPlayer(eduyayo);
//		controller.addPlayer(pigdroid);
//		controller.joinPlayer("eduyayo@gmail.com");
//		controller.joinPlayer("pigdroid@gmail.com");
//
////		String listString = "{104=BoardGameSelection [x=0, y=5, layer=1, selected=null], 106=BoardGameSelection [x=2, y=5, layer=1, selected=null], 108=BoardGameSelection [x=4, y=5, layer=1, selected=null], 110=BoardGameSelection [x=6, y=5, layer=1, selected=null]}";
////		String listString = "{113=BoardGameSelection [x=1, y=6, layer=1, selected=null], 115=BoardGameSelection [x=3, y=6, layer=1, selected=null], 117=BoardGameSelection [x=5, y=6, layer=1, selected=null], 119=BoardGameSelection [x=7, y=6, layer=1, selected=null]}";
////		String listString = "{65=BoardGameSelection [x=1, y=0, layer=null, selected=null], 67=BoardGameSelection [x=3, y=0, layer=null, selected=null], 69=BoardGameSelection [x=5, y=0, layer=null, selected=null], 71=BoardGameSelection [x=7, y=0, layer=null, selected=null], 72=BoardGameSelection [x=0, y=1, layer=null, selected=null], 74=BoardGameSelection [x=2, y=1, layer=null, selected=null], 76=BoardGameSelection [x=4, y=1, layer=null, selected=null], 78=BoardGameSelection [x=6, y=1, layer=null, selected=null], 81=BoardGameSelection [x=1, y=2, layer=null, selected=null], 83=BoardGameSelection [x=3, y=2, layer=null, selected=null], 85=BoardGameSelection [x=5, y=2, layer=null, selected=null], 87=BoardGameSelection [x=7, y=2, layer=null, selected=null]}";
//		String listString = "{65=BoardGameSelection [x=1, y=0, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=72]], 70=BoardGameSelection [x=6, y=0, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=72]], 72=BoardGameSelection [x=0, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 73=BoardGameSelection [x=1, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 74=BoardGameSelection [x=2, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 75=BoardGameSelection [x=3, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 76=BoardGameSelection [x=4, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 77=BoardGameSelection [x=5, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 78=BoardGameSelection [x=6, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]], 79=BoardGameSelection [x=7, y=1, layer=null, selected=null, toString()=GameSelection [playerName=null, modelId=80]]}";
//		Assert.assertEquals(listString, graphicContext.getSelectables().toString());
//		Map<Integer, GameSelection> selectables = graphicContext.getSelectables();
//		Player playerCheck = model.getCurrentPlayer();
//		Integer turnCheck = model.getCurrentTurn();
//		controller.checkpoint(); // Comment?
//		Assert.assertNull(graphicContext.getSelectables().get(controller.getZIndex(0, 0, 1)));
//		Assert.assertNotNull(graphicContext.getSelectables().get(controller.getZIndex(1, 0, 1)));
//		Assert.assertNotNull(graphicContext.getSelectables().get(controller.getZIndex(0, 1, 1)));
//		//select knight
//		controller.select(graphicContext.getSelectables().get(controller.getZIndex(1, 0, 1)));
//		Assert.assertNotNull(graphicContext.getSelectables().get(controller.getZIndex(0, 2, 1)));
//		Assert.assertNotNull(graphicContext.getSelectables().get(controller.getZIndex(2, 2, 1)));
//		controller.select(graphicContext.getSelectables().get(controller.getZIndex(2, 2, 1)));
//		while(controller.rollback()) {
//		};		
////TODO		Assert.assertEquals(((HumanPlayer) playerCheck).getEmail(), ((HumanPlayer)model.getCurrentPlayer()).getEmail());
////		Assert.assertEquals(turnCheck, model.getCurrentTurn());
////		Assert.assertEquals(
////				new TreeSet<Integer>(selectables.keySet()), 
////				new TreeSet<Integer>(graphicContext.getSelectables().keySet()));
//	}

	@Test
	public void testLoadSave() {

		controller = new ChessController() {
			@Override
			protected void onInitModel(ChessModel game) {
				super.onInitModel(game);
				model = game;
			}
		};
		
		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
			@Override
			public void onGamePaint(UIGameContext graphicContext) {
				ChessControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
			}
		});
		
		controller.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
			}

			public void onEndTurn(Player player) {
			}

		});

		model.setDirty(false);
		String json1 = model.memento().getData();
		model.setDirty(false);
		String saveGame1 = controller.getSerializedModel();
		model.setDirty(false);
		Assert.assertEquals(byteArrayToSortedList(json1), byteArrayToSortedList(model.memento().getData()));
		model.setDirty(false);
		Assert.assertEquals(byteArrayToSortedList(getSave(saveGame1)), byteArrayToSortedList(getSave(controller.getSerializedModel())));

		controller.checkpoint();
		json1 = model.memento().getData();
		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");
		while(controller.rollback());
		Assert.assertEquals(byteArrayToSortedList(json1), byteArrayToSortedList(model.memento().getData()));
	}

	
	
	private String getSave(String serializedModel) {
		Memento memento = null;
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(serializedModel));
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			memento = (Memento) objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return memento.getData();
	}

	private List<Byte> byteArrayToSortedList(String data) {
		List<Byte> ret = new ArrayList<Byte>();
		for (byte b : data.getBytes()) {
			ret.add(b);
		}
		Collections.sort(ret);
		return ret;
	}

//	@Test
//	public void testEndGame() {
//		controller = new ChessController() {
//			@Override
//			protected void onInitModel(ChessModel game) {
//				super.onInitModel(game);
//				model = game;
//				IntTileLayer layer = game.getLayer(1);
//				layer.clear(ChessModel.BLANK);
//				layer.set(0, 7, ChessModel.PAWN_WHITE);
//				layer.set(1, 6, ChessModel.PAWN_BLACK);
//				layer.set(3, 4, ChessModel.PAWN_BLACK);
//			}
//		};
//		
//		controller.setGameUIControllerListener(new GameControllerListenerAdapter(){
//			@Override
//			public void onGamePaint(UIGameContext graphicContext) {
//				ChessControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
//			}
//		});
//		
//		controller.setGameControllerListener(new GameControllerListenerAdapter() {
//			@Override
//			public void onEndGame() {
//				super.onEndGame();
//				gameEnded = true;
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
//		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext.getSelectables().get(it.next());
//		controller.select(selection);
//		Assert.assertEquals(1, graphicContext.getSelectables().size());
//		Assert.assertEquals(2, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getX());
//		Assert.assertEquals(5, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getY());
//		Assert.assertEquals(1, graphicContext.getSelections().size());
//		Assert.assertEquals(0, (int)model.getCurrentTurn());
//		controller.select(graphicContext.getSelectables().entrySet().iterator().next().getValue());
//		Assert.assertEquals(1, graphicContext.getSelectables().size());
//		Assert.assertEquals(0, (int)model.getCurrentTurn());
//		Assert.assertEquals(4, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getX());
//		Assert.assertEquals(3, (int)((TileBoardGameSelection) graphicContext.getSelectables().entrySet().iterator().next().getValue()).getY());
//		Assert.assertEquals(1, graphicContext.getSelections().size());
//		controller.select(graphicContext.getSelectables().entrySet().iterator().next().getValue());
//		Assert.assertTrue(gameEnded);
//	}
	
	//TODO test gameplay
	
	//TODO test check, undeo check, checkmate

}
