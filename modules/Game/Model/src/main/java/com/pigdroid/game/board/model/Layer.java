package com.pigdroid.game.board.model;

import com.pigdroid.game.model.Model;

public class Layer<T> extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer width;
	private Integer height;

	public Layer() {
	}
	
	public Layer(Integer width, Integer height) {
		this.width = width;
		this.height = height;
	}
	
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		if (this.width == null || !this.width.equals(width)) {
			this.width = width;
		}
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		if (this.height == null || !this.height.equals(height)) {
			this.height = height;
		}
	}

}
