package com.pigdroid.hub.model.persistent;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "gamestatistics")
@Table(name = "hub_gamestatistics")
public class GameStatistics {
	
	@Id 	
	@Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(cascade={CascadeType.ALL})
	private Usr usr;

	@Column
	private String gameName;

	@Column
	private Integer won = 0;
	
	@Column
	private Integer lost = 0;
	
	@Column
	private Integer tied = 0;
	
	@Column
	private Integer retired = 0;
	
	public Integer getTotal() {
		return won + lost + tied + retired;
	}

	public Usr getUsr() {
		return usr;
	}

	public void setUsr(Usr usr) {
		this.usr = usr;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getWon() {
		return won;
	}

	public void setWon(Integer won) {
		this.won = won;
	}

	public Integer getLost() {
		return lost;
	}

	public void setLost(Integer lost) {
		this.lost = lost;
	}

	public Integer getTied() {
		return tied;
	}

	public void setTied(Integer tied) {
		this.tied = tied;
	}

	public Integer getRetired() {
		return retired;
	}

	public void setRetired(Integer retired) {
		this.retired = retired;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void incRetired() {
		retired ++;
	}

	public void incWon() {
		won ++;
	}

	public void incLost() {
		lost ++;
	}

	public void incTied() {
		tied ++;
	}

}
