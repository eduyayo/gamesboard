package com.pigdroid.game.board.tile.checkers.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.model.UIBoardGameContext;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;

public class CheckersControllerByMessagesTest {

	private CheckersController controllerPigdroid;
	private UIBoardGameContext pigdroidUIGameContext;
	private CheckersController controllerEduyayo;
	private UIBoardGameContext eduyayoUIGameContext;
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

	@Before
	public void setup() {
		controllerPigdroid = new CheckersController();
		controllerPigdroid.newGame();
		controllerEduyayo = new CheckersController();
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
				System.out.println("controllerPigdroid sends data." + selectionsCopy.toString());
				Assert.assertTrue(selectionsCopy.size() > 1);
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
				System.out.println("controllerEduyayo sends data." + selectionsCopy.toString());
				Assert.assertTrue(selectionsCopy.size() > 1);
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
				pigdroidUIGameContext = (UIBoardGameContext) graphicContext;
				paint(graphicContext);
			}
			
		});
		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				System.out.println("controllerEduyayo drawing");
				eduyayoUIGameContext = (UIBoardGameContext) graphicContext;
				paint(graphicContext);
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
		UIBoardGameContext currentContext = eduyayoUIGameContext;
		CheckersController currentController = controllerEduyayo;
		int i = 0;
		Set<GameSelection> previousSelections = new HashSet<GameSelection>();
		while (!eduyayoUIGameContext.getSelectables().isEmpty() || !pigdroidUIGameContext.getSelectables().isEmpty()) {
			while (!currentContext.getSelectables().isEmpty()) {
				GameSelection selection = null;
				Iterator<Entry<Integer, GameSelection>> it = currentContext.getSelectables().entrySet().iterator();
				it.next();
				if (it.hasNext()) {
					selection = it.next().getValue();
					if (previousSelections.contains(selection)) {
						try {
							selection = currentContext.getSelectables().entrySet().iterator().next().getValue();
						} catch (Exception e) {
							
						}
					}
				} else {
					selection = currentContext.getSelectables().entrySet().iterator().next().getValue();
					if (previousSelections.contains(selection)) {
						try {
							selection = currentContext.getSelectables().entrySet().iterator().next().getValue();
						} catch (Exception e) {
							it = currentContext.getSelectables().entrySet().iterator();
							while (it.hasNext()) {
								selection = it.next().getValue();
							}
						}
					}
				}
				System.out.print(currentController == controllerEduyayo ? "eduyayo selects " : "pigdroid selects ");
				System.out.println(selection);
				currentController.select(selection);
				if (currentController == controllerEduyayo) {
					currentContext = (UIBoardGameContext) eduyayoUIGameContext;
				} else {
					currentContext = (UIBoardGameContext) pigdroidUIGameContext;
				}
				previousSelections.add(selection);
			}
			System.out.println("Movement " + (i++));
			currentController.commit();
			if (currentController == controllerEduyayo) {
				currentController = controllerPigdroid;
				currentContext = (UIBoardGameContext) pigdroidUIGameContext;
			} else {
				currentController = controllerEduyayo;
				currentContext = (UIBoardGameContext) eduyayoUIGameContext;
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
		
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(6, 1)));
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(7, 2)));
//		controllerEduyayo.commit();
//		
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.commit();
//		
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(7, 2)));
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(6, 3)));
//		controllerEduyayo.commit();
//		
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.commit();
//		
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(6, 3)));
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(7, 4)));
//		controllerEduyayo.commit();
//
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.select(controllerPigdroid.getSelectables().entrySet().iterator().next().getValue());
//		controllerPigdroid.commit();
//		
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(7, 4)));
//		controllerEduyayo.select(controllerEduyayo.getSelectables().get(key(6, 5)));
//		controllerEduyayo.commit();
//
//		controllerPigdroid.select(controllerPigdroid.getSelectables().get(key(5, 6)));
//		controllerPigdroid.select(controllerPigdroid.getSelectables().get(key(7, 4)));
//		controllerPigdroid.commit();

		Assert.assertTrue(gameEndedPigdroid);
		Assert.assertTrue(gameEndedEduyayo);
		Assert.assertFalse(pigdroidWinner);
		Assert.assertTrue(eduyayoWinner);
	}
	
	private int key(int x, int y) {
		return x + y * 8 + 8*8;
	}

	
//	@Test
//	public void testReject() {
//
//		controllerPigdroid.setGameControllerListener(new GameControllerListener() {
//
//			public void onPlayerJoined(Player invited) {
//				controllerEduyayo.joinPlayer(((HumanPlayer) invited).getEmail());
//			}
//
//			public void onPlayerLeft(Player found) {
//				controllerEduyayo.leavePlayer(((HumanPlayer) found).getEmail());			
//			}
//
//			public void onMove(List<GameSelection> selections) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void onStartGame() {
//				gameStartedPigdroid = true;
//			}
//
//			public void onSendSelections(List<GameSelection> selectionsCopy) {
//				System.out.println("controllerPigdroid sends data." + selectionsCopy.toString());
//				Assert.assertTrue(selectionsCopy.size() > 1);
//				controllerEduyayo.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
//			}
//			
//			public boolean onIsPlayerLocal(List<GameSelection> moved) {
//				if (moved != null && !moved.isEmpty()) {
//					return onIsPlayerLocal(moved.get(0).getPlayerName());
//				}
//				return false;
//			}
//
//			public boolean onIsPlayerLocal(String email) {
//				return "pigdroid@gmail.com".equals(email);
//			}
//
//			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
//				gameEndedPigdroid = true;
//				pigdroidWinner = winner && "pigdroid".equals(currentPlayer.getName());
//			}
//
//			public void onNeverReadyToStart() {
//				pigdroidNeverReady = true;
//				
//			}
//
//			public void onSelect(List<GameSelection> selections) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		controllerPigdroid.setTurnGameControllerListener(new TurnGameControllerListener() {
//
//			public void onStartTurn(Player player) {
//				System.out.println("controllerPigdroid:: Starts turn for " + player.getName());
//			}
//
//			public void onEndTurn(Player player) {
//				System.out.println("controllerPigdroid:: ENDS turn for " + player.getName());
//				
//			}
//		});
//		
//		controllerEduyayo.setGameControllerListener(new GameControllerListener() {
//
//			public void onPlayerJoined(Player invited) {
//				controllerPigdroid.joinPlayer(((HumanPlayer) invited).getEmail());
//			}
//
//			public void onPlayerLeft(Player found) {
//				controllerPigdroid.leavePlayer(((HumanPlayer) found).getEmail());			
//			}
//
//			public void onMove(List<GameSelection> selections) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void onStartGame() {
//				gameStartedEduyayo = true;
//				
//			}
//
//			public void onSendSelections(List<GameSelection> selectionsCopy) {
//				System.out.println("controllerEduyayo sends data." + selectionsCopy.toString());
//				Assert.assertTrue(selectionsCopy.size() > 1);
//				controllerPigdroid.select(selectionsCopy.toArray(new GameSelection[selectionsCopy.size()]));
//			}
//
//			public boolean onIsPlayerLocal(List<GameSelection> moved) {
//				if (moved != null && !moved.isEmpty()) {
//					return onIsPlayerLocal(moved.get(0).getPlayerName());
//				}
//				return false;
//			}
//
//			public boolean onIsPlayerLocal(String email) {
//				return "eduyayo@gmail.com".equals(email);
//			}
//
//			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
//				gameEndedEduyayo = true;
//				eduyayoWinner = winner && "eduyayo".equals(currentPlayer.getName());
//				
//			}
//
//			public void onNeverReadyToStart() {
//				eduyayoNeverReady = true;
//				
//			}
//
//			public void onSelect(List<GameSelection> selections) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		controllerEduyayo.setTurnGameControllerListener(new TurnGameControllerListener() {
//
//			public void onStartTurn(Player player) {
//				System.out.println("controllerEduyayo:: Starts turn for " + player.getName());
//				
//			}
//
//			public void onEndTurn(Player player) {
//				System.out.println("controllerEduyayo:: ENDS turn for " + player.getName());
//				
//			}
//		});
//
//		controllerPigdroid.setGameUIControllerListener(new GameUIControllerListener() {
//
//
//			public void onGamePaint(UIGameContext graphicContext) {
//				System.out.println("controllerPigdroid drawing");
//				pigdroidUIGameContext = (UIBoardGameContext) graphicContext;
//				paint(graphicContext);
//			}
//			
//		});
//		controllerEduyayo.setGameUIControllerListener(new GameUIControllerListener() {
//
//			public void onGamePaint(UIGameContext graphicContext) {
//				System.out.println("controllerEduyayo drawing");
//				eduyayoUIGameContext = (UIBoardGameContext) graphicContext;
//				paint(graphicContext);
//			}
//		});
//
//		HumanPlayer eduyayo = new HumanPlayer("eduyayo", "eduyayo@gmail.com");
//		HumanPlayer pigdroid = new HumanPlayer("pigdroid", "pigdroid@gmail.com");
//		
//		controllerEduyayo.newGame();
//
//		controllerEduyayo.addPlayer(eduyayo);
//		controllerEduyayo.addPlayer(pigdroid);
//		controllerPigdroid.loadModelFromSerialized(controllerEduyayo.getSerializedModel());
//
//		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
//		
//		controllerEduyayo.joinPlayer("eduyayo@gmail.com");
//		Assert.assertNotNull(controllerPigdroid.getInvitedPlayer());
//		controllerEduyayo.leavePlayer("pigdroid@gmail.com");
//		Assert.assertNull(controllerEduyayo.getInvitedPlayer());
//		Assert.assertTrue(pigdroidNeverReady);
//		Assert.assertTrue(eduyayoNeverReady);
//	}
	
	private void paint(UIGameContext graphicContext) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				int zs = x + y * 8 + 1 * 8 * 8;
				GameSelection piece = graphicContext.getUiElements().get(zs);
				GameSelection selection = ((UIBoardGameContext) graphicContext).getSelectables().get(zs);
				if (selection != null) {
					System.out.print("(");
				} else {
					System.out.print(" ");
				}
				if (piece != null) {
					System.out.print(piece.getModelId());
				} else {
					System.out.print("O");
				}
				if (selection != null) {
					System.out.print(")");
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	
}
