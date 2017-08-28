package com.pigdroid.hub.model.persistent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pigdroid.social.model.BaseEntity;
import com.pigdroid.social.model.user.User;

@Entity(name = "usr")
@Table(name = "hub_usr")
public class Usr extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id 	
	@Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String deviceToken;
	
	@Basic(fetch = FetchType.LAZY)
	@ManyToMany(cascade={CascadeType.PERSIST})
	private Set<Usr> contacts = new HashSet<Usr>();

	@Basic(fetch = FetchType.LAZY)
	@ManyToMany(cascade={CascadeType.PERSIST}, mappedBy = "users")
	private Set<Game> games;

    @OneToOne(cascade={CascadeType.PERSIST})
	private User user;

	@Basic(fetch = FetchType.LAZY)
	@ManyToMany(cascade={CascadeType.PERSIST}, mappedBy = "contacts")
    private Set<Usr> inverseContacts;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "usr")
	@MapKey(name = "gameName")
	private Map<String, GameStatistics> gameStatistics = new HashMap<String, GameStatistics>();
	
    public Set<Usr> getInverseContacts() {
		return inverseContacts;
	}
    
    public void setInverseContacts(Set<Usr> inverseContacts) {
		this.inverseContacts = inverseContacts;
	}
    
    public User getUser() {
		return user;
	}
    
    public void setUser(User user) {
		this.user = user;
	}

	public Set<Game> getGames() {
		if (games == null) {
			games = new HashSet<Game>();
		}
		return games;
	}
	
	public Usr add(Game game) {
		getGames().add(game);
		game.add(this);
		return this;
	}

	public void setGames(Set<Game> games) {
		this.games = games;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long uuid) {
		this.id = uuid;
	}

	public Set<Usr> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Usr> contactList) {
		this.contacts = contactList;
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
		Usr other = (Usr) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Map<String, GameStatistics> getGameStatistics() {
		return gameStatistics;
	}

	public void setGameStatistics(Map<String, GameStatistics> gameStatistics) {
		this.gameStatistics = gameStatistics;
	}

}
