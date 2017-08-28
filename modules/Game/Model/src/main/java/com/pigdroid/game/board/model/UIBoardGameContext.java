package com.pigdroid.game.board.model;

import java.util.List;
import java.util.Map;

import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.turn.model.UITurnGameContext;

public class UIBoardGameContext extends UITurnGameContext {

	private Map<Integer, GameSelection> selectables;

	public UIBoardGameContext(Map<Integer, GameSelection> uiElements,
			Map<Integer, GameSelection> selectables,
			List<GameSelection> selections) {
		super(uiElements, selections);
		this.setSelectables(selectables);
	}

	public Map<Integer, GameSelection> getSelectables() {
		return selectables;
	}

	public void setSelectables(Map<Integer, GameSelection> selectables) {
		this.selectables = selectables;
	}
	
}

