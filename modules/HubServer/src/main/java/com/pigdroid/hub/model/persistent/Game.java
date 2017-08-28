package com.pigdroid.hub.model.persistent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "game")
public class Game implements PersistentResourceInterface {

	private static final long serialVersionUID = 5077504848648478821L;

	@Id
	@Column(name = "id", unique = true)
	private String id;

	@Column
	private Date timestamp;
	
	@Column
	private String gameName;
	
	@Column
	private GameEstateEnum estate = GameEstateEnum.WAITING_PLAYERS;
	
	@ManyToMany
	@Basic(fetch = FetchType.LAZY)
	private Set<User> users;

	public Set<User> getUsers() {
		if (users == null) {
			users = new HashSet<User>();
		}
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Column(name = "save_game", columnDefinition = "TEXT")
	private String saveGame;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String uuid) {
		this.id = uuid;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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

	public void setSyncValue(Long syncValue) {
		this.syncValue = syncValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public GameEstateEnum getEstate() {
		return estate;
	}
	
	public void setEstate(GameEstateEnum estate) {
		this.estate = estate;
	}

	public void add(User user) {
		getUsers().add(user);
		user.getGames().add(this);
	}
	
}
