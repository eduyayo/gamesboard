package com.pigdroid.game.board.tile.dots.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.tile.dots.model.DotsModel;
import com.pigdroid.game.board.tile.dots.model.DotsSelection;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;

public class DotsControllerByMessagesTest {

	private DotsController controllerPigdroid;
	private DotsController controllerEduyayo;
	private UITileBoardGameContext graphicContextPigdroid;
	private UITileBoardGameContext graphicContextEduyayo;
	private boolean gameEndedPigdroid = false;
	private boolean gameEndedEduyayo = false;
	private boolean gameStartedPigdroid = false;
	private boolean gameStartedEduyayo = false;
	private boolean turnEndedPigdroid = false;
	private boolean turnEndedEduyayo = false;
	private boolean eduyayoWinner = false;
	private boolean pigdroidWinner = false;
	private boolean pigdroidNeverReady = false;
	private boolean eduyayoNeverReady = false;
	
	private List<?> eduyayoReceived = null;
	private List<?> pigdroidReceived = null;

	@Before
	public void setup() {
		controllerPigdroid = new DotsController();
		controllerPigdroid.newGame();
		controllerEduyayo = new DotsController();
		controllerEduyayo.newGame();
		gameEndedPigdroid = false;
		gameEndedEduyayo = false;
		gameStartedPigdroid = false;
		gameStartedEduyayo = false;
		turnEndedPigdroid = false;
		turnEndedEduyayo = false;
	}

	@Test
	public void testStartGame() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		the1000moves();
		
	}

	private void the1000moves() {
		controllerPigdroid.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerEduyayo.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerEduyayo.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				gameStartedPigdroid = true;
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerPigdroid sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerEduyayo.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
				
			}
			
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "pigdroid@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedPigdroid = true;
				pigdroidWinner = winner && "pigdroid".equals(currentPlayer.getName());
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerPigdroid.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerPigdroid:: Starts turn for " + player.getName());
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerPigdroid:: ENDS turn for " + player.getName());
				
			}
		});
		
		controllerEduyayo.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerPigdroid.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerPigdroid.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {

				selections = null;				
			}

			public void onStartGame() {
				gameStartedEduyayo = true;
				
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerEduyayo sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerPigdroid.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
			}

			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "eduyayo@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedEduyayo = true;
				eduyayoWinner = winner && "eduyayo".equals(currentPlayer.getName());
				
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerEduyayo.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerEduyayo:: Starts turn for " + player.getName());
				
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerEduyayo:: ENDS turn for " + player.getName());
				
			}
		});

		controllerPigdroid.setGameUIControllerListener(new GameUIControllerListener() {


			public void onGamePaint(UIGameContext graphicContext) {
				System.out.println("controllerPigdroid drawing");
				for (int y = 0; y < 17; y++) {
					for (int x = 0; x < 17; x++) {
						int zs = controllerPigdroid.getZIndex(x,  y,  1);
						GameSelection piece = graphicContext.getUiElements().get(zs);
						if (piece != null) {
							System.out.print(piece.getModelId());
						} else {
							System.out.print("O");
						}
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
			}
			
		});
		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				// TODO Auto-generated method stub
				
			}
		});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		
		controllerEduyayo.newGame();

		controllerEduyayo.addPlayer(eduyayo);
		controllerEduyayo.addPlayer(pigdroid);
		controllerPigdroid.loadModelFromSerialized(controllerEduyayo.getSerializedModel());

		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		
		controllerEduyayo.joinPlayer("eduyayo@gmail.com");
		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		controllerEduyayo.joinPlayer("pigdroid@gmail.com");
		Assert.assertNull(controllerEduyayo.getInvitedPlayer());
		Assert.assertNull(controllerPigdroid.getInvitedPlayer());

		Assert.assertTrue(gameStartedEduyayo);
		Assert.assertTrue(gameStartedPigdroid);

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		
		Assert.assertTrue(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertFalse(controllerEduyayo.getSelectables().isEmpty());

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		System.out.println(controllerPigdroid.getCurrentPlayerIndex());
		DotsController currentController = controllerEduyayo;
		int i = 0;
		Set<GameSelection> previousSelections = new HashSet<GameSelection>();
		while (!controllerEduyayo.getSelectables().isEmpty() || !controllerPigdroid.getSelectables().isEmpty()) {
			while (!currentController.getSelectables().isEmpty()) {
				GameSelection selection = null;
				Iterator<Entry<Integer, GameSelection>> it = currentController.getSelectables().entrySet().iterator();
				it.next();
				if (it.hasNext()) {
					selection = it.next().getValue();
					if (previousSelections.contains(selection)) {
						try {
							selection = currentController.getSelectables().entrySet().iterator().next().getValue();
						} catch (Exception e) {
							
						}
					}
				} else {
					selection = currentController.getSelectables().entrySet().iterator().next().getValue();
					if (previousSelections.contains(selection)) {
						try {
							selection = currentController.getSelectables().entrySet().iterator().next().getValue();
						} catch (Exception e) {
							it = currentController.getSelectables().entrySet().iterator();
							while (it.hasNext()) {
								selection = it.next().getValue();
							}
						}
					}
				}
				System.out.println(selection);
				currentController.select(selection);
				previousSelections.add(selection);
			}
			System.out.println("Movement " + (i++));
			currentController.commit();
			if (currentController == controllerEduyayo) {
				currentController = controllerPigdroid;
			} else {
				currentController = controllerEduyayo;
			}
			if (i > 999) {
				break;
			}
		}
	}
	

	@Test
	public void testEndGame() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		the1000moves();
		
		Assert.assertTrue(gameEndedPigdroid);
		Assert.assertTrue(gameEndedEduyayo);
		Assert.assertFalse(pigdroidWinner);
		Assert.assertFalse(eduyayoWinner);
	}
	
	private int key(int x, int y) {
		return x + y * 17 + 17*17;
	}

	
	@Test
	public void testReject() {

		controllerPigdroid.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerEduyayo.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerEduyayo.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				gameStartedPigdroid = true;
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerPigdroid sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerEduyayo.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
				
			}
			
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "pigdroid@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedPigdroid = true;
				pigdroidWinner = winner && "pigdroid".equals(currentPlayer.getName());
			}

			public void onNeverReadyToStart() {
				pigdroidNeverReady = true;
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerPigdroid.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerPigdroid:: Starts turn for " + player.getName());
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerPigdroid:: ENDS turn for " + player.getName());
				
			}
		});
		
		controllerEduyayo.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerPigdroid.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerPigdroid.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				gameStartedEduyayo = true;
				
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerEduyayo sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerPigdroid.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
			}

			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "eduyayo@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedEduyayo = true;
				eduyayoWinner = winner && "eduyayo".equals(currentPlayer.getName());
				
			}

			public void onNeverReadyToStart() {
				eduyayoNeverReady = true;
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerEduyayo.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerEduyayo:: Starts turn for " + player.getName());
				
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerEduyayo:: ENDS turn for " + player.getName());
				
			}
		});

		controllerPigdroid.setGameUIControllerListener(new GameUIControllerListener() {


			public void onGamePaint(UIGameContext graphicContext) {
				System.out.println("controllerPigdroid drawing");
				for (int y = 0; y < 17; y++) {
					for (int x = 0; x < 17; x++) {
						int zs = controllerPigdroid.getZIndex(x,  y,  1);
						GameSelection piece = graphicContext.getUiElements().get(zs);
						if (piece != null) {
							System.out.print(piece.getModelId());
						} else {
							System.out.print("O");
						}
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
			}
			
		});
		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				// TODO Auto-generated method stub
				
			}
		});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		
		controllerEduyayo.newGame();

		controllerEduyayo.addPlayer(eduyayo);
		controllerEduyayo.addPlayer(pigdroid);
		controllerPigdroid.loadModelFromSerialized(controllerEduyayo.getSerializedModel());

		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		
		controllerEduyayo.joinPlayer("eduyayo@gmail.com");
		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		controllerEduyayo.leavePlayer("pigdroid@gmail.com");
		Assert.assertNull(controllerEduyayo.getInvitedPlayer());
		Assert.assertTrue(pigdroidNeverReady);
		Assert.assertTrue(eduyayoNeverReady);
	}
	
	
	@Test
	public void test1move() {
		controllerPigdroid.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerEduyayo.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerEduyayo.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				gameStartedPigdroid = true;
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerPigdroid sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerEduyayo.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
				
			}
			
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "pigdroid@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedPigdroid = true;
				pigdroidWinner = winner && "pigdroid".equals(currentPlayer.getName());
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerPigdroid.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerPigdroid:: Starts turn for " + player.getName());
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerPigdroid:: ENDS turn for " + player.getName());
				
			}
		});
		
		controllerEduyayo.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerPigdroid.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerPigdroid.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {

				selections = null;				
			}

			public void onStartGame() {
				gameStartedEduyayo = true;
				
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerEduyayo sends data.");
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerPigdroid.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
			}

			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "eduyayo@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedEduyayo = true;
				eduyayoWinner = winner && "eduyayo".equals(currentPlayer.getName());
				
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerEduyayo.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerEduyayo:: Starts turn for " + player.getName());
				
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerEduyayo:: ENDS turn for " + player.getName());
				
			}
		});

		controllerPigdroid.setGameUIControllerListener(new GameUIControllerListener() {


			public void onGamePaint(UIGameContext graphicContext) {
				System.out.println("controllerPigdroid drawing");
				for (int y = 0; y < 17; y++) {
					for (int x = 0; x < 17; x++) {
						int zs = controllerPigdroid.getZIndex(x,  y,  1);
						GameSelection piece = graphicContext.getUiElements().get(zs);
						if (piece != null) {
							System.out.print(piece.getModelId());
						} else {
							System.out.print("O");
						}
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
			}
			
		});
		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				// TODO Auto-generated method stub
				
			}
		});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		
		controllerEduyayo.newGame();

		controllerEduyayo.addPlayer(eduyayo);
		controllerEduyayo.addPlayer(pigdroid);
		controllerPigdroid.loadModelFromSerialized(controllerEduyayo.getSerializedModel());

		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		
		controllerEduyayo.joinPlayer("eduyayo@gmail.com");
		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		controllerEduyayo.joinPlayer("pigdroid@gmail.com");
		Assert.assertNull(controllerEduyayo.getInvitedPlayer());
		Assert.assertNull(controllerPigdroid.getInvitedPlayer());

		Assert.assertTrue(gameStartedEduyayo);
		Assert.assertTrue(gameStartedPigdroid);

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		
		Assert.assertTrue(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertFalse(controllerEduyayo.getSelectables().isEmpty());

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		System.out.println(controllerPigdroid.getCurrentPlayerIndex());
		DotsController currentController = controllerEduyayo;
		int i = 0;
		Assert.assertFalse(controllerEduyayo.getSelectables().isEmpty());
		Assert.assertTrue(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertFalse(gameEndedPigdroid);
		Assert.assertFalse(gameEndedEduyayo);
		controllerEduyayo.select(controllerEduyayo.getSelectables().values().iterator().next());
		Assert.assertFalse(gameEndedPigdroid);
		Assert.assertFalse(gameEndedEduyayo);
		Assert.assertTrue(controllerEduyayo.getSelectables().isEmpty());
		Assert.assertFalse(controllerPigdroid.getSelectables().isEmpty());
	}
	
	
	@Test
	public void test1Square() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		controllerPigdroid.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerEduyayo.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerEduyayo.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				gameStartedPigdroid = true;
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerPigdroid sends data.");
				eduyayoReceived = selectionsCopy;
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerEduyayo.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
				
			}
			
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "pigdroid@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedPigdroid = true;
				pigdroidWinner = winner && "pigdroid".equals(currentPlayer.getName());
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerPigdroid.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerPigdroid:: Starts turn for " + player.getName());
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerPigdroid:: ENDS turn for " + player.getName());
				
			}
		});
		
		controllerEduyayo.setGameControllerListener(new GameControllerListener() {

			public void onPlayerJoined(Player invited) {
				controllerPigdroid.joinPlayer(((HumanPlayer) invited).getEmail());
			}

			public void onPlayerLeft(Player found) {
				controllerPigdroid.leavePlayer(((HumanPlayer) found).getEmail());			
			}

			public void onMove(List<GameSelection> selections) {

				selections = null;				
			}

			public void onStartGame() {
				gameStartedEduyayo = true;
				
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				System.out.println("controllerEduyayo sends data.");
				pigdroidReceived = selectionsCopy;
				Assert.assertNotNull(selectionsCopy);
				Assert.assertFalse(selectionsCopy.isEmpty());
				Assert.assertFalse(((DotsSelection) selectionsCopy.get(selectionsCopy.size() - 1)).isClose());
				controllerPigdroid.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
			}

			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				if (moved != null && !moved.isEmpty()) {
					return onIsPlayerLocal(moved.get(0).getPlayerName());
				}
				return false;
			}

			public boolean onIsPlayerLocal(String email) {
				return "eduyayo@gmail.com".equals(email);
			}

			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				gameEndedEduyayo = true;
				eduyayoWinner = winner && "eduyayo".equals(currentPlayer.getName());
				
			}

			public void onNeverReadyToStart() {
				// TODO Auto-generated method stub
				
			}

			public void onSelect(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}
		});
		controllerEduyayo.setTurnGameControllerListener(new TurnGameControllerListener() {

			public void onStartTurn(Player player) {
				System.out.println("controllerEduyayo:: Starts turn for " + player.getName());
				
			}

			public void onEndTurn(Player player) {
				System.out.println("controllerEduyayo:: ENDS turn for " + player.getName());
				
			}
		});

		controllerPigdroid.setGameUIControllerListener(new GameUIControllerListener() {


			public void onGamePaint(UIGameContext graphicContext) {
				System.out.println("controllerPigdroid drawing");
				for (int y = 0; y < 17; y++) {
					for (int x = 0; x < 17; x++) {
						int zs = controllerPigdroid.getZIndex(x,  y,  1);
						GameSelection piece = graphicContext.getUiElements().get(zs);
						if (piece != null) {
							System.out.print(piece.getModelId());
						} else {
							System.out.print("O");
						}
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
			}
			
		});
		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				// TODO Auto-generated method stub
				
			}
		});

		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
		
		controllerEduyayo.newGame();

		controllerEduyayo.addPlayer(eduyayo);
		controllerEduyayo.addPlayer(pigdroid);
		controllerPigdroid.loadModelFromSerialized(controllerEduyayo.getSerializedModel());

		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		
		controllerEduyayo.joinPlayer("eduyayo@gmail.com");
		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
		controllerEduyayo.joinPlayer("pigdroid@gmail.com");
		Assert.assertNull(controllerEduyayo.getInvitedPlayer());
		Assert.assertNull(controllerPigdroid.getInvitedPlayer());

		Assert.assertTrue(gameStartedEduyayo);
		Assert.assertTrue(gameStartedPigdroid);

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		
		Assert.assertTrue(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertFalse(controllerEduyayo.getSelectables().isEmpty());

		Assert.assertEquals("eduyayo", controllerPigdroid.getCurrentPlayerName());
		System.out.println(controllerPigdroid.getCurrentPlayerIndex());
		DotsController currentController = controllerEduyayo;
		int i = 0;
		Assert.assertFalse(controllerEduyayo.getSelectables().isEmpty());
		Assert.assertTrue(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertFalse(gameEndedPigdroid);
		Assert.assertFalse(gameEndedEduyayo);

		controllerEduyayo.select(controllerEduyayo.getSelectables().get(controllerEduyayo.getZIndex(1, 0, 1))); System.out.println(controllerEduyayo.getSelectables());
		controllerPigdroid.select(controllerPigdroid.getSelectables().get(controllerEduyayo.getZIndex(2, 1, 1)));
		controllerEduyayo.select(controllerEduyayo.getSelectables().get(controllerEduyayo.getZIndex(0, 1, 1)));
		controllerPigdroid.select(controllerPigdroid.getSelectables().get(controllerEduyayo.getZIndex(1, 2, 1)));
		Assert.assertFalse(controllerPigdroid.getSelectables().isEmpty());
		Assert.assertTrue(controllerEduyayo.getSelectables().isEmpty());
		controllerPigdroid.select(controllerPigdroid.getSelectables().values().iterator().next());

		Field field = GameController.class.getDeclaredField("model");
		field.setAccessible(true);

		DotsModel model = (DotsModel) field.get(controllerEduyayo);
		Assert.assertEquals("5", Integer.toString(model.getLayer(1).get(1, 1)));

		model = (DotsModel) field.get(controllerPigdroid);
		Assert.assertEquals("5", Integer.toString(model.getLayer(1).get(1, 1)));
		
		Assert.assertEquals(2, eduyayoReceived.size());
	}
	
}
