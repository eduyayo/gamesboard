package com.pigdroid.game.controller;

import java.util.List;

import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;

public class GameControllerListenerAdapter implements GameControllerListener, GameUIControllerListener {

	@Override
	public void onGamePaint(UIGameContext graphicContext) {

	}

	@Override
	public void onPlayerJoined(Player invited) {

	}

	@Override
	public void onPlayerLeft(Player found) {

	}

	@Override
	public void onMove(List<GameSelection> selections) {
		
	}

	@Override
	public void onStartGame() {
		
	}

	@Override
	public void onSendSelections(List<GameSelection> selectionsCopy) {
		
	}

	@Override
	public boolean onIsPlayerLocal(List<GameSelection> moved) {
		if (moved != null && moved.size() > 0) {
			return onIsPlayerLocal(moved.get(0).getPlayerName());
		}
		return false;
	}

	@Override
	public void onEndGame(Player player, boolean winner, boolean loser, boolean tie) {
		
	}

	@Override
	public void onNeverReadyToStart() {
		
	}

	@Override
	public boolean onIsPlayerLocal(String email) {
		return false;
	}

	@Override
	public void onSelect(List<GameSelection> selections) {
		// TODO Auto-generated method stub
		
	}

}