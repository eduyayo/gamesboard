package com.pigdroid.game.board.tile;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.board.controller.BoardGameController;
import com.pigdroid.game.board.tile.model.UITileBoardGameContext;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.turn.controller.TurnGameController;
import com.pigdroid.game.turn.controller.TurnGameController.TurnGameControllerListener;

public abstract class ControllerByMessagesAbstractTest {

	protected TurnGameController<?> controllerPigdroid;
	protected TurnGameController<?> controllerEduyayo;
	protected UITileBoardGameContext graphicContextPigdroid;
	protected UITileBoardGameContext graphicContextEduyayo;
	protected boolean gameEndedPigdroid = false;
	protected boolean gameEndedEduyayo = false;
	protected boolean gameStartedPigdroid = false;
	protected boolean gameStartedEduyayo = false;
	protected boolean turnEndedPigdroid = false;
	protected boolean turnEndedEduyayo = false;
	protected boolean eduyayoWinner = false;
	protected boolean pigdroidWinner = false;
	protected boolean pigdroidNeverReady = false;
	protected boolean eduyayoNeverReady = false;

	@Before
	public void setup() {
		controllerPigdroid = createController();
		controllerPigdroid.newGame();
		controllerEduyayo = createController();
		controllerEduyayo.newGame();
		gameEndedPigdroid = false;
		gameEndedEduyayo = false;
		gameStartedPigdroid = false;
		gameStartedEduyayo = false;
		turnEndedPigdroid = false;
		turnEndedEduyayo = false;
	}

	protected abstract TurnGameController<?> createController();
	
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
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						int zs = x + y * 8 + 1 * 8 * 8;
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
		TurnGameController<?> currentController = controllerEduyayo;
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
	
	protected int key(int x, int y, TurnGameController<?> controller) {
		return ((BoardGameController<?, ?, ?>) controller).getZIndex(x, y, 1);
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
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						int zs = x + y * 8 + 1 * 8 * 8;
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
}
