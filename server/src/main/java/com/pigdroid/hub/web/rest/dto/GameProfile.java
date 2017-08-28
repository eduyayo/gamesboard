
package com.pigdroid.hub.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pigdroid.hub.model.persistent.GameEstateEnum;

public class GameProfile extends ClientProfile {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String saveGame;
	private String modelId;
	private String gameName;
	private List<Map<String, String>> players;
	private GameEstateEnum gameEstate;
	
	public GameEstateEnum getGameEstate() {
		return gameEstate;
	}
	
	public void setGameEstate(GameEstateEnum gameEstate) {
		this.gameEstate = gameEstate;
	}
	
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	
	public String getModelId() {
		return modelId;
	}

	@Override
	public String getType() {
		return "game";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public String getSaveGame() {
		return saveGame;
	}

	public void setSaveGame(String saveGame) {
		this.saveGame = saveGame;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public static class Builder {
		private String saveGame;
		private String modelId;
		private String gameName;
		private GameEstateEnum gameEstate;
		private List<Map<String, String>> players = new ArrayList<Map<String, String>>();
		private Map<String, String> currentPlayer = null;
		
		public Builder gameEstate(GameEstateEnum gameEstate) {
			this.gameEstate = gameEstate;
			return this;
		}
		
		public Builder gameName(String gameName) {
			this.gameName = gameName;
			return this;
		}
		
		public Builder modelId(String id) {
			this.modelId = id;
			return this;
		}
		
		public Builder modelId(Long id) {
			this.modelId = id.toString();
			return this;
		}
		
		public Builder saveGame(String saveGame) {
			this.saveGame = saveGame;
			return this;
		}
		
		private Builder() {
			
		}
		
		public GameProfile build() {
			GameProfile ret = new GameProfile();
			ret.setGameName(gameName);
			ret.setModelId(modelId);
			ret.setSaveGame(saveGame);
			ret.setPlayers(players);
			ret.setGameEstate(gameEstate);
			return ret;
		}
		
		public Builder player() {
			createPlayer();
			return this;
		}

		private void createPlayer() {
			currentPlayer = new HashMap<String, String>();
			players.add(currentPlayer);
		}

		public Builder email(String email) {
			createPlayerIf();
			currentPlayer.put("email", email);
			return this;
		}

		private void createPlayerIf() {
			if (currentPlayer == null) {
				createPlayer();
			}
		}

		public Builder status(String value) {
			createPlayerIf();
			currentPlayer.put("status", value);
			return this;
		}
		
	}

	public void setPlayers(List<Map<String, String>> players) {
		this.players = players;
	}
	
	public List<Map<String, String>> getPlayers() {
		return players;
	}

}
