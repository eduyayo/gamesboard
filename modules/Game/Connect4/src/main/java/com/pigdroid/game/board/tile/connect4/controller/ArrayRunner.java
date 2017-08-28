package com.pigdroid.game.board.tile.connect4.controller;

import com.pigdroid.game.board.tile.model.TileLayer;

public class ArrayRunner {

	public static interface Visitor<T> {
		void visit(int x, int y, T element);
	}

	public static interface LineVisitor {
		void visit();
	}
	
	private ArrayRunner() {
	}
	
	public static final <T> void run(TileLayer <T> layer, Visitor<T> onEachElement, LineVisitor onLineEnd) {
		int width = layer.getWidth();
		int height = layer.getHeight();
		int max = width + height - 1;
		
		//left to right down up
		for (int i = 0; i < max; i++) {
			boolean ended = false;
			boolean started = false;
			for (int c = 0; c <= i; c++) {
				int x = c; 
				int y = i - c;
				if (x < width && y < height) {
					//It is within
					onEachElement.visit(x, y, layer.get(x, y));
					started = true;
				} else if (started) {
					if (!ended) {
						onLineEnd.visit();
						ended = true;
					}
				}
			}
			if (!ended) {
				onLineEnd.visit();
			}
		}
		
		//right to left down up
		for (int i = 0; i < max; i++) {
			boolean ended = false;
			boolean started = false;
			for (int c = 0; c <= i; c++) {
				int x = c; 
				int y = i - c;
				if (x < width && y < height) {
					//It is within
					onEachElement.visit(width - x - 1, y, layer.get(width - x - 1, y));
					started = true;
				} else if (started) {
					if (!ended) {
						onLineEnd.visit();
						ended = true;
					}
				}
			}
			if (!ended) {
				onLineEnd.visit();
			}
		}
		
		//horizontal run
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				onEachElement.visit(x, y, layer.get(x, y));
			}
			onLineEnd.visit();
		}
		
		//vertical run
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				onEachElement.visit(x, y, layer.get(x, y));
			}
			onLineEnd.visit();
		}
	}

//	public static void main(String[] args){
//		IntTileLayer layer = new IntTileLayer(3, 3);
//		ArrayRunner.run(layer, new Visitor<Integer>() {
//			public void visit(int x, int y, Integer element) {
//				System.out.print(x + " " + y + " _");
//			}
//		}, new LineVisitor() {
//			public void visit() {
//				System.out.println(";");
//			}
//		});
//	}
	
}
