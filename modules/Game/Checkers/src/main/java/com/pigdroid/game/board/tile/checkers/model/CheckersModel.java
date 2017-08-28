package com.pigdroid.game.board.tile.checkers.model;

import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;

public class CheckersModel extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int BK_BLACK	= -1;
	public static final int BK_WHITE	= -2;

	public static final int BLANK		= 0;
	public static final int PAWN_WHITE	= 1;
	public static final int QUEEN_WHITE	= 2;
	public static final int PAWN_BLACK	= 3;
	public static final int QUEEN_BLACK	= 4;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CheckersSelection selectionToKeep;

	public CheckersModel() {
		super(8, 8, BLANK);
	}
	
	@Override
	protected IntTileLayer createLayer() {
		return new IntTileLayer(8, 8, 0);
	}

	public void setSelectionToKeep(CheckersSelection selectionToKeep) {
		this.selectionToKeep = selectionToKeep;
	}
	
	public CheckersSelection getSelectionToKeep() {
		return selectionToKeep;
	}
	
//	@Override
//	protected JSONSerializer createSerializer() {
//		JSONSerializer ret = super.createSerializer();
//		ret.transform(new IterableTransformer() {
//			   public void transform(Object object) {
//		            Iterable iterable = (Iterable) object;
//		            JSONContext ctx = getContext();
//		            TypeContext typeContext = ctx.writeOpenArray();
//		            for (Object item : iterable) {
//		                if (!typeContext.isFirst()) ctx.writeComma();
//		                ctx.write
//		            }
//		            getContext().writeCloseArray();
//		        }
//		}, "");
//		return ret ;
//	}
	
}
