package com.pigdroid.game.board.model;

import com.pigdroid.game.turn.model.TurnGameSelection;

public class BoardGameSelection extends TurnGameSelection {

	private static final long serialVersionUID = 1L;
	
	private Integer x;
	private Integer y;
	private Integer layer;
	
	private Long selected;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getLayer() {
		return layer;
	}

	public void setLayer(Integer layer) {
		this.layer = layer;
	}

	public Long getSelected() {
		return selected;
	}

	public void setSelected(Long selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "BoardGameSelection [x=" + x + ", y=" + y + ", layer=" + layer
				+ ", selected=" + selected + ", toString()=" + super.toString()
				+ "]";
	}
	
	@Override
	public int hashCode() {
		return (x.toString() + y.toString() + getPlayerName()).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return Integer.valueOf(hashCode()).equals(obj.hashCode());
	}
	

}
