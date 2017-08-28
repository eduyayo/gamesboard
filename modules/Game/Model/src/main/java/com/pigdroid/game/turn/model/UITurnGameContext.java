package com.pigdroid.game.turn.model;

import java.util.List;
import java.util.Map;

import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.UIGameContext;

public class UITurnGameContext extends UIGameContext {

	public UITurnGameContext(Map<Integer, GameSelection> uiElements,
			List<GameSelection> selections) {
		super(uiElements, selections);
	}

}
