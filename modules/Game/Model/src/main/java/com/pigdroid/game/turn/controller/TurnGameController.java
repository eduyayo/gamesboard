package com.pigdroid.game.turn.controller;

import java.util.Map;

import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.turn.model.TurnGameModel;

public abstract class TurnGameController<Game extends TurnGameModel> extends GameController<Game> {

	public static interface TurnGameControllerListener {
		void onStartTurn(Player player);
		
		/**
		 * Should call commit() or allow user options for committing.
		 * @param player 
		 */
		void onEndTurn(Player player);
	}
	
	private TurnGameControllerListener turnGameControllerListener = new TurnGameControllerListenerAdapter() {
		@Override
		public void onEndTurn(Player player) {
			commit();
		}
	};
	
	protected void selectableOrThrow(GameSelection gameSelection) {
		int playerIndex = getModel().getCurrentTurn();
		Map<Integer, GameSelection> selectables = getSelectables(playerIndex);
		if (selectables.get(getIndex(gameSelection)) == null) {
			throw new IllegalArgumentException("Cannot select, the selection is not a selectable.");
		}
	}

	
	public boolean isCurrentTurn(String playerName) {
		boolean ret = false;
			if (playerName != null) {
			Player current = getModel().getCurrentPlayer();
			if (current != null) {
				if (current instanceof HumanPlayer) {
					ret = ((HumanPlayer) current).getEmail().equals(playerName);
				} else {
					ret = current.getName().equals(playerName);
				}
			}
		}
		return ret;
	}
	
	public void doStartTurn() {
		getModel().getSelections().clear();
		checkpoint();
		doNextTurn();
		if (turnGameControllerListener != null) {
			turnGameControllerListener.onStartTurn(getModel().getCurrentPlayer());
		}
	}
	
	private void doNextTurn() {
		Game game = getModel();
		int currentTurn = game.getCurrentTurn() + 1;
		if (currentTurn >= game.getPlayers().size()) {
			game.setRoundCount(game.getRoundCount() + 1);
			currentTurn = 0;
		}
		game.setCurrentTurn(currentTurn);
		game.setTurnCount(game.getTurnCount() + 1);
	}

	protected void doEndTurn() {
		Game game = getModel();
		clearSelections(true);
		if (turnGameControllerListener != null) {
			turnGameControllerListener.onEndTurn(game.getCurrentPlayer());
		} else {
			game.commit();
		}
		Player p = game.getCurrentPlayer();
		if (isGameEnd(p)) {
			doEndGame(p);
		}
		game.setDirty(true);
	}

	public String getCurrentPlayerName() {
		Game game = getModel();
		return game.getCurrentPlayer().getName();
	}

	public String getCurrentPlayerEmail() {
		Game game = getModel();
		Player player = game.getCurrentPlayer();
		return player instanceof HumanPlayer ? ((HumanPlayer) player).getEmail() : null;
	}
	
	public int getCurrentPlayerIndex() {
		Game game = getModel();
		return game.getCurrentTurn();
	}
	
	public int getPlayerIndex(String email) {
		Game game = getModel();
		int i = 0;
		boolean found= false;
		for (Player player : game.getPlayers()) {
			if (player instanceof HumanPlayer) {
				if (((HumanPlayer) player).getEmail().equals(email)) {
					found = true;
					break;
				}
			}
			i++;
		}
		return found ? i : -1;
	}

	public boolean isTurnDone() {
		return getModel().isMoved();
	}
	
	@Override
	public void joinPlayer(String email) {
		super.joinPlayer(email);
		if (getInvitedPlayer() == null) {
			getModel().setCurrentTurn(-1);
			doStartTurn();
		}
	}
	
	@Override
	public void doTick(Player currentPlayer) {
		doMove(currentPlayer);
		doTurn();
		doPaint();
	}

	protected void doTurn() {
		if (isTurnDone()) {
			doEndTurn();
			doStartTurn();
		}
	}
	
	protected void doMove(Player currentPlayer) {
		if (isMove()) {
			move();
//			clearSelections(true);
//			if (isGameEnd(currentPlayer)) {
//				doEndGame(currentPlayer);
//			}
		}
	}

	public void setTurnGameControllerListener(TurnGameControllerListener turnGameControllerListener) {
		this.turnGameControllerListener = turnGameControllerListener;
	}
	
	@Override
	public void select(GameSelection... gameSelections) {
		super.select(gameSelections); 
		if (isMove()) {
			getModel().setMoved(true);
		}
	}
	
}
