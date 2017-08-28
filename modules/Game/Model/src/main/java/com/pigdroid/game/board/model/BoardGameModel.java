package com.pigdroid.game.board.model;

import java.util.ArrayList;
import java.util.List;

import com.pigdroid.game.turn.model.TurnGameModel;

public abstract class BoardGameModel<TileType, LayerType extends Layer<TileType>> extends TurnGameModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer width;
	private Integer height;
	
	private List<LayerType> layers = new ArrayList<LayerType>();

	public BoardGameModel(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public BoardGameModel() {
		
	}

	protected int addLayer(LayerType layer) {
		layers.add(layer);
		return layers.indexOf(layer);
	}
	
	public int addLayer() {
		LayerType layer = createLayer();
		layers.add(layer);
		return layers.indexOf(layer);
	}
	
	protected abstract LayerType createLayer();

	public LayerType getLayer(int index) {
		return layers.get(index);
	}
	
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public List<LayerType> getLayers() {
		return layers;
	}
	
	public int getLayerCount() {
		return layers.size();
	}

	public void setLayers(List<LayerType> pLayers) {
		this.layers.clear();
		this.layers.addAll(pLayers);
	}
	
}