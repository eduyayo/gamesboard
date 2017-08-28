package com.pigdroid.game.board.tile.checkers.controller;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.checkers.model.CheckersModel;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameSelection;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.controller.GameControllerListenerAdapter;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.model.memento.Memento;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;
import com.pigdroid.game.turn.controller.TurnGameControllerListenerAdapter;
import com.pigdroid.util.Base64;

public class CheckersControllerTest {

	private CheckersController controller;
	private UITileBoardGameContext graphicContext;
	private CheckersModel model;
	private boolean gameEnded = false;
	private boolean turnEnded = false;
	private List<GameSelection> sendSelections;

	@Before
	public void setup() {
		controller = new CheckersController();
		controller.newGame();
		turnEnded = false;
		sendSelections = null;
	}

	@Test
	public void testStartGame() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		GameControllerListener gameListener = createMock(GameControllerListener.class);
		controller.setGameControllerListener(gameListener);

		GameUIControllerListener uiListener = createMock(GameUIControllerListener.class);
		controller.setGameUIControllerListener(uiListener);

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");

		// Record listener
		gameListener.onPlayerJoined(eduyayo);
		gameListener.onIsPlayerLocal("eduyayo@gmail.com");
		expectLastCall().andAnswer(new IAnswer<Boolean>() {

			public Boolean answer() throws Throwable {
				return true;
			}

		}).anyTimes();
		uiListener.onGamePaint((UIGameContext) anyObject());
		gameListener.onPlayerJoined(pigdroid);
		uiListener.onGamePaint((UIGameContext) anyObject());
		gameListener.onStartGame();

		replay(gameListener, uiListener);
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");
		verify(gameListener);
		verify(uiListener);

		Field field = GameController.class.getDeclaredField("model");
		field.setAccessible(true);
		CheckersModel model = (CheckersModel) field.get(controller);
		Assert.assertNotNull(model);
		Assert.assertEquals(eduyayo, model.getCurrentPlayer());
		Assert.assertNotNull(model.getCurrentPlayer());

		Method getSelectables = CheckersController.class
				.getDeclaredMethod("getSelectables");
		getSelectables.setAccessible(true);
		Object value = getSelectables.invoke(controller);
		Assert.assertNotNull(value);

		// controller.doForcePaint();
	}

	@Test
	public void testFirstMove() {
		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}
				});

		controller
				.setGameControllerListener(new GameControllerListenerAdapter() {

					@Override
					public boolean onIsPlayerLocal(List<GameSelection> moved) {
						return true;
					}

					@Override
					public boolean onIsPlayerLocal(String email) {
						return true;
					}

				});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		Iterator<Integer> it = graphicContext.getSelectables().keySet()
				.iterator();
		it.next();
		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext
				.getSelectables().get(it.next());
		int x = selection.getX();
		int y = selection.getY();
		controller.select(selection);
		boolean foundTopLeft = false;
		boolean foundTopRight = false;
		int count = 0;
		for (int key : graphicContext.getSelectables().keySet()) {
			selection = (TileBoardGameSelection) graphicContext
					.getSelectables().get(key);
			count++;
			if (selection.getX() == x - 1 && selection.getY() == y - 1) {
				foundTopLeft = true;
			}
			if (selection.getX() == x + 1 && selection.getY() == y - 1) {
				foundTopRight = true;
			}
		}
		Assert.assertTrue(foundTopLeft);
		Assert.assertTrue(foundTopRight);
		Assert.assertEquals(2, count);
	}

	@Test
	public void testDoubleJump() {
		controller = new CheckersController() {
			@Override
			protected void onInitModel(CheckersModel game) {
				super.onInitModel(game);
				model = game;
				IntTileLayer layer = game.getLayer(1);
				layer.clear(CheckersModel.BLANK);
				layer.set(0, 7, CheckersModel.PAWN_WHITE);
				layer.set(1, 6, CheckersModel.PAWN_BLACK);
				layer.set(3, 4, CheckersModel.PAWN_BLACK);
			}
		};

		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {

					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}

				});

		controller
				.setGameControllerListener(new GameControllerListenerAdapter() {

					@Override
					public boolean onIsPlayerLocal(List<GameSelection> moved) {
						return true;
					}

					@Override
					public boolean onIsPlayerLocal(String email) {
						return true;
					}

					@Override
					public void onSendSelections(
							List<GameSelection> selectionsCopy) {
						sendSelections = selectionsCopy;
					}

				});

		controller
				.setTurnGameControllerListener(new TurnGameControllerListenerAdapter() {
					@Override
					public void onEndTurn(Player player) {
						turnEnded = true;
					}

				});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		Iterator<Integer> it = graphicContext.getSelectables().keySet()
				.iterator();
		TileBoardGameSelection selection = (TileBoardGameSelection) graphicContext
				.getSelectables().get(it.next());
		controller.select(selection); // choose the piece
		Assert.assertEquals(1, graphicContext.getSelectables().size());
		Assert.assertEquals(2,
				(int) ((TileBoardGameSelection) graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getX());
		Assert.assertEquals(5,
				(int) ((TileBoardGameSelection) graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getY());
		Assert.assertEquals(false, turnEnded);
		Assert.assertEquals(1, graphicContext.getSelections().size());
		Assert.assertEquals(0, (int) model.getCurrentTurn());
		controller.select(graphicContext.getSelectables().entrySet().iterator()
				.next().getValue()); // jump!
		Assert.assertEquals(false, turnEnded);
		Assert.assertEquals(1, graphicContext.getSelectables().size());
		Assert.assertEquals(0, (int) model.getCurrentTurn());
		Assert.assertEquals(4,
				(int) ((TileBoardGameSelection) graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getX());
		Assert.assertEquals(3,
				(int) ((TileBoardGameSelection) graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getY());
		Assert.assertEquals(1, graphicContext.getSelections().size());
		controller.select(graphicContext.getSelectables().entrySet().iterator()
				.next().getValue()); // jump again!
		Assert.assertEquals(true, turnEnded);
		Assert.assertEquals(3, sendSelections.size());
	}

	@Test
	public void testDoubleJumpAndQueen() {
		controller = new CheckersController() {
			@Override
			protected void onInitModel(CheckersModel game) {
				super.onInitModel(game);
				model = game;
			}

		};

		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}
				});

		controller
				.setGameControllerListener(new GameControllerListenerAdapter() {
					@Override
					public boolean onIsPlayerLocal(List<GameSelection> moved) {
						return true;
					}

					@Override
					public boolean onIsPlayerLocal(String email) {
						return true;
					}
				});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 5, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(5, 4, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(5, 2, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 3, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(3, 6, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 5, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(1, 2, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(0, 3, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 7, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(3, 6, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(0, 1, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(1, 2, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(6, 5, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(7, 4, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 3, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(6, 5, 1)));
		controller.select(graphicContext.getSelectables().get(
				controller.getZIndex(4, 7, 1)));

		Assert.assertEquals(0, (int) model.getCurrentTurn());

	}

	@Test
	public void testUndoCommit() {

		controller = new CheckersController() {
			@Override
			protected void onInitModel(CheckersModel game) {
				super.onInitModel(game);
				model = game;
			}
		};

		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}
				});

		controller
				.setTurnGameControllerListener(new TurnGameControllerListener() {

					public void onStartTurn(Player player) {

					}

					public void onEndTurn(Player player) {
						
					}

				});

		controller
				.setGameControllerListener(new GameControllerListenerAdapter() {
					@Override
					public boolean onIsPlayerLocal(List<GameSelection> moved) {
						return true;
					}

					@Override
					public boolean onIsPlayerLocal(String email) {
						return true;
					}
				});

		model.setDirty(true);
		String control1 = controller.getSerializedModel();

		controller.checkpoint();
		model.setDirty(true);
		Assert.assertEquals(control1, controller.getSerializedModel());
		controller.rollback();
		model.setDirty(true);
		Assert.assertEquals(byteArrayToSortedList(getSave(control1)),
				byteArrayToSortedList(getSave(controller.getSerializedModel())));

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		String listString = "{104=BoardGameSelection [x=0, y=5, layer=1, selected=null, toString()=GameSelection [playerName=eduyayo@gmail.com, modelId=1]], 106=BoardGameSelection [x=2, y=5, layer=1, selected=null, toString()=GameSelection [playerName=eduyayo@gmail.com, modelId=1]], 108=BoardGameSelection [x=4, y=5, layer=1, selected=null, toString()=GameSelection [playerName=eduyayo@gmail.com, modelId=1]], 110=BoardGameSelection [x=6, y=5, layer=1, selected=null, toString()=GameSelection [playerName=eduyayo@gmail.com, modelId=1]]}";
		Assert.assertEquals(listString, graphicContext.getSelectables()
				.toString());
		Map<Integer, GameSelection> selectables = graphicContext
				.getSelectables();
//TODO
//		controller.commit();
//		
//		Player playerCheck = model.getCurrentPlayer();
//		Integer turnCheck = model.getCurrentTurn();
//		// controller.checkpoint();
//		Assert.assertNotNull(graphicContext.getSelectables().get(
//				controller.getZIndex(4, 5, 1)));
//		controller.select(graphicContext.getSelectables().get(
//				controller.getZIndex(4, 5, 1)));
//		Assert.assertNotNull(graphicContext.getSelectables().get(
//				controller.getZIndex(5, 4, 1)));
//		controller.select(graphicContext.getSelectables().get(
//				controller.getZIndex(5, 4, 1)));
//		while (controller.rollback()) {
//		}
//		;
//		Assert.assertEquals(((HumanPlayer) playerCheck).getEmail(),
//				((HumanPlayer) model.getCurrentPlayer()).getEmail());
//		Assert.assertEquals(turnCheck, model.getCurrentTurn());
//		Assert.assertEquals(new TreeSet<Integer>(selectables.keySet()),
//				new TreeSet<Integer>(graphicContext.getSelectables().keySet()));
	}

	@Test
	public void testLoadSave() {

		controller = new CheckersController() {
			@Override
			protected void onInitModel(CheckersModel game) {
				super.onInitModel(game);
				model = game;
			}
		};

		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}
				});

		controller
				.setTurnGameControllerListener(new TurnGameControllerListener() {

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
		Assert.assertEquals(byteArrayToSortedList(json1),
				byteArrayToSortedList(model.memento().getData()));
		model.setDirty(false);
		Assert.assertEquals(byteArrayToSortedList(getSave(saveGame1)),
				byteArrayToSortedList(getSave(controller.getSerializedModel())));

		controller.checkpoint();
		json1 = model.memento().getData();
		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");
		while (controller.rollback())
			;
		Assert.assertEquals(byteArrayToSortedList(json1),
				byteArrayToSortedList(model.memento().getData()));
	}

	private String getSave(String serializedModel) {
		Memento memento = null;
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					Base64.decode(serializedModel));
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
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

	@Test
	public void testEndGame() {
		controller = new CheckersController() {
			@Override
			protected void onInitModel(CheckersModel game) {
				super.onInitModel(game);
				model = game;
				IntTileLayer layer = game.getLayer(1);
				layer.clear(CheckersModel.BLANK);
				layer.set(0, 7, CheckersModel.PAWN_WHITE);
				layer.set(1, 6, CheckersModel.PAWN_BLACK);
				layer.set(3, 4, CheckersModel.PAWN_BLACK);
			}
		};

		controller
				.setGameUIControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onGamePaint(UIGameContext graphicContext) {
						CheckersControllerTest.this.graphicContext = (UITileBoardGameContext) graphicContext;
					}

				});

		controller
				.setGameControllerListener(new GameControllerListenerAdapter() {
					@Override
					public void onEndGame(Player currentPlayer, boolean winner, boolean loser,
							boolean tie) {
						super.onEndGame(currentPlayer, winner, loser, tie);
						gameEnded = true;
					}

					@Override
					public boolean onIsPlayerLocal(List<GameSelection> moved) {
						return true;
					}

					@Override
					public boolean onIsPlayerLocal(String email) {
						return true;
					}
				});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		controller.addPlayer(eduyayo);
		controller.addPlayer(pigdroid);
		controller.joinPlayer("eduyayo@gmail.com");
		controller.joinPlayer("pigdroid@gmail.com");

		Iterator<Integer> it = CheckersControllerTest.this.graphicContext.getSelectables().keySet()
				.iterator();
		TileBoardGameSelection selection = (TileBoardGameSelection) CheckersControllerTest.this.graphicContext
				.getSelectables().get(it.next());
		controller.select(selection);
		Assert.assertEquals(1, CheckersControllerTest.this.graphicContext.getSelectables().size());
		Assert.assertEquals(2,
				(int) ((TileBoardGameSelection) CheckersControllerTest.this.graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getX());
		Assert.assertEquals(5,
				(int) ((TileBoardGameSelection) CheckersControllerTest.this.graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getY());
		Assert.assertEquals(1, CheckersControllerTest.this.graphicContext.getSelections().size());
		Assert.assertEquals(0, (int) model.getCurrentTurn());
		controller.select(CheckersControllerTest.this.graphicContext.getSelectables().entrySet().iterator()
				.next().getValue());
		Assert.assertEquals(1, CheckersControllerTest.this.graphicContext.getSelectables().size());
		Assert.assertEquals(0, (int) model.getCurrentTurn());
		Assert.assertEquals(4,
				(int) ((TileBoardGameSelection) CheckersControllerTest.this.graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getX());
		Assert.assertEquals(3,
				(int) ((TileBoardGameSelection) CheckersControllerTest.this.graphicContext.getSelectables()
						.entrySet().iterator().next().getValue()).getY());
		Assert.assertEquals(1, CheckersControllerTest.this.graphicContext.getSelections().size());
		controller.select(graphicContext.getSelectables().entrySet().iterator().next().getValue());
		Assert.assertTrue(gameEnded);
	}

}
