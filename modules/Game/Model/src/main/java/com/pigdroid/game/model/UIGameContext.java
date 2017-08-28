package com.pigdroid.game.model;

import java.util.List;
import java.util.Map;

public class UIGameContext {

	private Map<Integer, GameSelection> uiElements;
	private List<GameSelection> selections;
	private int rotationDegreesPerPlayer = 180;

	public UIGameContext(Map<Integer, GameSelection> uiElements,
			List<GameSelection> selections) {
		this.setUiElements(uiElements);
		this.setSelections(selections);
	}

	public Map<Integer, GameSelection> getUiElements() {
		return uiElements;
	}

	public void setUiElements(Map<Integer, GameSelection> uiElements) {
		this.uiElements = uiElements;
	}

	public List<GameSelection> getSelections() {
		return selections;
	}

	public void setSelections(List<GameSelection> selections) {
		this.selections = selections;
	}

	public int getRotationDegreesPerPlayer() {
		return rotationDegreesPerPlayer;
	}

	public void setRotationDegreesPerPlayer(int rotationDegreesPerPlayer) {
		this.rotationDegreesPerPlayer = rotationDegreesPerPlayer;
	}

}
