package com.pigdroid.game.board.tile.chess.model;

import com.pigdroid.game.board.tile.chess.controller.ChessHelper;
import com.pigdroid.game.board.tile.model.IntTileLayer;
import com.pigdroid.game.board.tile.model.TileBoardGameModel;

public class ChessModel extends TileBoardGameModel<Integer, IntTileLayer> {

	public static final int BK_BLACK	= -1;
	public static final int BK_WHITE	= -2;

	public static final int BLANK		= ChessHelper.EMPTY;
	
	public static final int PAWN_WHITE	= ChessHelper.PAWN;
	public static final int TOWER_WHITE	= ChessHelper.TOWER;
	public static final int HORSE_WHITE	= ChessHelper.KNIGHT;
	public static final int BISHOP_WHITE= ChessHelper.BISHOP;
	public static final int QUEEN_WHITE	= ChessHelper.QUEEN;
	public static final int KING_WHITE	= ChessHelper.KING;
	
	public static final int PAWN_BLACK	= Character.toUpperCase(ChessHelper.PAWN);
	public static final int TOWER_BLACK	= Character.toUpperCase(ChessHelper.TOWER);
	public static final int HORSE_BLACK	= Character.toUpperCase(ChessHelper.KNIGHT);
	public static final int BISHOP_BLACK= Character.toUpperCase(ChessHelper.BISHOP);
	public static final int QUEEN_BLACK	= Character.toUpperCase(ChessHelper.QUEEN);
	public static final int KING_BLACK	= Character.toUpperCase(ChessHelper.KING);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean moved = Boolean.FALSE;

	public ChessModel() {
		super(8, 8, BLANK);
	}

	public void setMoved(Boolean b) {
		this.moved = b;
	}

	public boolean isMoved() {
		return moved;
	}
	
	public Boolean getMoved() {
		return moved;
	}

	@Override
	protected IntTileLayer createLayer() {
		return new IntTileLayer(8, 8, BLANK);
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
