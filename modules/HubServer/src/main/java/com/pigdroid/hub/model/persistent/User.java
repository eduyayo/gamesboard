package com.pigdroid.hub.model.persistent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "usr")
public class User implements PersistentResourceInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 	
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true)
	private String id;
	private Date timestamp;
	
	@Column (unique = true)
	private String email;
	private String password;
	private String nickName;
	
	@OneToMany
	@Basic(fetch = FetchType.LAZY)
	private Set<User> contacts = new HashSet<User>();

	@ManyToMany
	@Basic(fetch = FetchType.LAZY)
	private Set<Game> games;

	@ManyToMany
	@Basic(fetch = FetchType.LAZY)
	private Set<Message> messages;

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Set<Game> getGames() {
		if (games == null) {
			games = new HashSet<Game>();
		}
		return games;
	}
	
	public User add(Game game) {
		getGames().add(game);
		game.add(this);
		return this;
	}

	public void setGames(Set<Game> games) {
		this.games = games;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String uuid) {
		this.id = uuid;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Set<User> getContacts() {
		return contacts;
	}

	public void setContacts(Set<User> contactList) {
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
