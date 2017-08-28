package com.pigdroid.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7657122931889837844L;
	
	private List<Player> players = new ArrayList<Player>();
	
	private boolean dirty = true;

	private List<GameSelection> selections = new ArrayList<GameSelection>();
	private List<GameSelection> movedSelections = new ArrayList<GameSelection>();

	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}
	
	public Player getPlayer(String playerName) {
		Player ret = null;
		if (playerName != null && !"".equals(playerName)) {
			for (Player player : players) {
				if (player instanceof HumanPlayer) {
					if (playerName.equals(((HumanPlayer) player).getEmail())) {
						ret = player; 
						break;
					} else if (playerName.equals(player.getName())) {
						ret = player;
						break;
					}
				}
			}
		}
		return ret;
	}
	
	public void setPlayers(List<Player> players) {
		this.players.clear();
		this.players.addAll(players);
	}
	
	public int addPlayer(Player player) {
		players.add(player);
		return players.indexOf(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}
	
	public void removePlayer(int player) {
		players.remove(player);
	}

	public Player findPlayer(String playerName) {
		Player found = null;
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				found = player;
			}
		}
		return found;
	}
	
	public int getPlayerCount() {
		return players.size();
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public List<GameSelection> getSelections() {
		return selections;
	}

	public void setSelections(List<GameSelection> selections) {
		this.selections = selections;
	}

	public List<GameSelection> getMovedSelections() {
		return movedSelections;
	}

	public void setMovedSelections(List<GameSelection> moved) {
		this.movedSelections = moved;
	}
	
}
