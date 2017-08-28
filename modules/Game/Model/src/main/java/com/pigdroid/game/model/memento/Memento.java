package com.pigdroid.game.model.memento;

import java.io.Serializable;

public class Memento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String data;

	public static final Memento EMPTY = new Memento();

	public Memento() {
		data = "{}";
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return data.equals(((Memento) obj).data);
	}
	
}
