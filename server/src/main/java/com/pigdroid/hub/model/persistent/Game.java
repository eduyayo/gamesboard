package com.pigdroid.hub.model.persistent;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.pigdroid.social.model.BaseEntity;
import com.pigdroid.social.model.user.User;

@Entity
@Table(name = "hub_game")
public class Game extends BaseEntity<Long> {

	private static final long serialVersionUID = 5077504848648478821L;

	@Id
	@Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String gameName;
	
	@Column
	private GameEstateEnum estate = GameEstateEnum.WAITING_PLAYERS;
	

	@Basic(fetch = FetchType.LAZY)
	@ManyToMany(cascade= CascadeType.ALL)
	private Set<Usr> users;

	public Set<Usr> getUsers() {
		if (users == null) {
			users = new HashSet<Usr>();
		}
		return users;
	}

	public void setUsers(Set<Usr> users) {
		this.users = users;
	}

	@Column(name = "save_game", columnDefinition = "TEXT")
	private String saveGame;

	public Long getId() {
		return id;
	}

	public void setId(Long uuid) {
		this.id = uuid;
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

	public void add(Usr user) {
		getUsers().add(user);
		user.getGames().add(this);
	}

	public void remove(Usr user) {
		getUsers().remove(user);
		user.getGames().add(this);
	}

	public void add(User user) {
		add(user.getUsr());
	}

	public void remove(User user) {
		remove(user.getUsr());
	}
	
}
