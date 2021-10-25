package com.pigdroid.game.model.memento;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class UndoableObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private Deque<Memento> mementos = new ArrayDeque<Memento>();

	@JsonIgnore
	public Memento memento() {
		Memento ret = null;
		ObjectMapper serializer = createSerializer();
		ret = new Memento();
		try {
			ret.setData(serializer.writeValueAsString(this));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	protected ObjectMapper createSerializer() {
		ObjectMapper ret = new ObjectMapper();
		return ret.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public void from(Memento memento) {
		ObjectMapper mapper = createSerializer();
		ObjectReader reader = mapper.readerForUpdating(this);
		try {
			reader.readValue(Memento.EMPTY.getData());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		if (memento != null) {
			try {
				reader.readValue(memento.getData());
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void checkpoint() {
		mementos.push(memento());
	}

	public boolean rollback() {
		boolean ret = false;
		if (!mementos.isEmpty()) {
			from(mementos.pop());
			ret = true;
		}
		return ret;
	}

	public void commit() {
		mementos.clear();
	}

	public void from(UndoableObject from) {
		this.from(from.memento());
	}

	public UndoableObject copy() {
		UndoableObject ret = null;
		try {
			ret = this.getClass().newInstance();
			ret.from(memento());
		} catch (Exception e) {
		}
		return ret;
	}

}
