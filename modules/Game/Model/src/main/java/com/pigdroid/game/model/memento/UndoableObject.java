package com.pigdroid.game.model.memento;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class UndoableObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Deque<Memento> mementos = new ArrayDeque<Memento>(); 
	
	public Memento memento() {
		Memento ret = null;
		JSONSerializer serializer = createSerializer();
		ret = new Memento();
		ret.setData(serializer.deepSerialize(this));
		return ret;
	}

	protected JSONSerializer createSerializer() {
		return new JSONSerializer().exclude("memento", "mementos");
	}

	public void from(Memento memento) {
		JSONDeserializer<UndoableObject> deserializer = new JSONDeserializer<UndoableObject>();
		deserializer.deserializeInto(Memento.EMPTY.getData(), this);
		if (memento != null) {
			deserializer.deserializeInto(memento.getData(), this);
		}
	}

	public void checkpoint() {
		mementos.push(memento());
	}
	
	public boolean rollback() {
		boolean ret = false;
		if (!mementos.isEmpty()) {
			from(mementos.pop());
			ret  = true;
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
