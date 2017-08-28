package com.pigdroid.game.board.tile.chess.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pigdroid.game.board.tile.chess.model.ChessSelection;
import com.pigdroid.game.board.tile.model.IntTileLayer;

public class ChessHelper {

	public static final int EMPTY	= 0;
	
	public static final int PAWN	= 'p';
	public static final int TOWER	= 't';
	public static final int KNIGHT	= 'h';
	public static final int BISHOP	= 'b';
	public static final int KING	= 'k';
	public static final int QUEEN	= 'q';

	public static final int CHECK_LEVEL_NONE	= 0;
	public static final int CHECK_LEVEL_CHECK	= 1;
	public static final int CHECK_LEVEL_MATE	= 2;
	
	private static abstract class Move {

		public boolean canMove(int x, int y, IntTileLayer layer) {
			int [][] coords = getCoordinates(x, y, layer);
			int max = coords.length;
			for (int i = 0; i < max; i++) {
				int [] coord = coords[i];
				if (canMoveTo(x, y, x + coord[0], y + coord[1], layer)) {
//					System.out.println((Character.valueOf((char) layer.get(x, y).intValue())) + " at " + x + ", " + y + " CAN move to " + (x + coord[0]) + ", " + (y + coord[1]));
					return true;
				} else {
//					System.out.println((Character.valueOf((char) layer.get(x, y).intValue())) + " at " + x + ", " + y + " cannot move to " + (x + coord[0]) + ", " + (y + coord[1]));
				}
			}
			return false;
		}
		
		public boolean canMoveTo(int srcx, int srcy, int tox, int toy, IntTileLayer layer) {
			if (tox < 0) {
//				System.out.println("cannot move out of bounds 1");
				return false;
			}
			if (toy < 0) {
//				System.out.println("cannot move out of bounds 2");
				return false;
			}
			if (srcx == tox && srcy == toy) {
//				System.out.println("cannot move to the same pos");
				return false;
			}
			int width = layer.getWidth();
			if (width <= tox) {
//				System.out.println("cannot move out of bounds 3");
				return false;
			}
			int height = layer.getHeight();
			if (height <= toy) {
//				System.out.println("cannot move out of bounds 4");
				return false;
			}
			int c = layer.get(srcx, srcy);
			// See if eating!
			int dx = layer.get(tox, toy);
			if (!isEmpty(dx) && !isEnemies(c, dx)) {
//				System.out.println("cannot move on an ally piece");
				return false;
			}
			// See if jumping (only horse jumps)
			if (!isKnight(c) && isJumping(srcx, srcy, tox, toy, layer)) {
//				System.out.println("cannot move, only horses jump!");
				return false;
			} else if(isPawn(c) && srcx == tox && isPiece(dx)) {
				//pawns cannot eat going forward
				return false;
			}
			return true;
		}
		
//		private boolean isJumping(int srcx, int srcy, int tox, int toy, IntTileLayer layer) {
//			int xinc = tox - srcx;
//			if (xinc < 0) {
//				xinc = -1;
//			} else if (xinc > 0) {
//				xinc = 1;
//			}
//			int yinc = toy - srcy;
//			if (yinc < 0) {
//				yinc = -1;
//			} else if (yinc > 0) {
//				yinc = 1;
//			}
//			int c = layer.get(srcx, srcy);
//			int dc = layer.get(tox, toy);
//			if (!isEmpty(dc) && !isEnemies(c, dc)) {
//				return true;
//			}
//			int x = srcx + xinc;
//			int y = srcy + yinc;
//			do {
//				do {
//					Integer val = layer.get(x, y);
//					if (val != null) {
//						if (isPiece(val)) {
//							return true;
//						}
//					}
//				} while (y != toy && ((y += yinc) != toy));
//			} while (y != toy && x != tox && ((x += xinc) != tox) );
//			return false;
//		}

		private boolean isJumping(int srcx, int srcy, int tox, int toy, IntTileLayer layer) {
			int loopx = srcx < tox ? srcx + 1 : srcx > tox ? srcx - 1 : srcx;
			int loopy = srcy < toy ? srcy + 1 : srcy > toy ? srcy - 1 : srcy;
			while ((loopx != tox) || (loopy != toy)) {
				Integer val = layer.get(loopx, loopy);
				if (val != null) {
					if (isPiece(val)) {
						return true;
					}
				}
				loopx = loopx < tox ? loopx + 1 : loopx > tox ? loopx - 1 : loopx;
				loopy = loopy < toy ? loopy + 1 : loopy > toy ? loopy - 1 : loopy;
			}
			return false;
		}

		public List<ChessSelection> getMoves(int x, int y, IntTileLayer layer) {
			List<ChessSelection> ret = new ArrayList<ChessSelection>();
			int [][] coords = getCoordinates(x, y, layer);
			int max = coords.length;
			for (int i = 0; i < max; i++) {
				int [] coord = coords[i];
				if (canMoveTo(x, y, x + coord[0], y + coord[1], layer)) {
					int xt = x + coord[0];
					int yt = y + coord[1];
					ChessSelection sel = createSelection(null, (long) layer.get(xt, yt), xt, yt, 1);
					ret.add(sel);
				}
			}
			return ret;
		}

		protected abstract int [][] getCoordinates(int x, int y, IntTileLayer layer);
		
	}
	
	private static Map<Integer, Move> movesPerPiece;
	
	static {
		// PAWN
		movesPerPiece = new HashMap<Integer, Move>();
		movesPerPiece.put((int) 'P', new Move() {
			private int [][] startingCoords = {
					{0, 1},
					{0, 2},
			};
			
			private int [][] coords = {
					{0, 1}
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				if (y == 1) {
					return startingCoords;
				}
				return coords;
			}
		});
		movesPerPiece.put((int) 'p', new Move() {
			private int [][] startingCoords = {
					{0, -1},
					{0, -2},
			};
			
			private int [][] coords = {
					{0, -1}
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				if (y == 6) {
					return startingCoords;
				}
				return coords;
			}
		});


		// TOWER
		Move adapter = new Move() {
			private int [][] coords = {
					{0, 1},
					{0, 2},
					{0, 3},
					{0, 4},
					{0, 5},
					{0, 6},
					{0, 7},
					{0, 8},
					{1, 0},
					{2, 0},
					{3, 0},
					{4, 0},
					{5, 0},
					{6, 0},
					{7, 0},
					{8, 0},
					{0, -1},
					{0, -2},
					{0, -3},
					{0, -4},
					{0, -5},
					{0, -6},
					{0, -7},
					{0, -8},
					{-1, 0},
					{-2, 0},
					{-3, 0},
					{-4, 0},
					{-5, 0},
					{-6, 0},
					{-7, 0},
					{-8, 0},
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				return coords;
			}
		};
		movesPerPiece.put((int) 'T', adapter);
		movesPerPiece.put((int) 't', adapter);

		// HORSE
		adapter = new Move() {
			private int [][] coords = {
					{1, 2},
					{1, -2},
					{-1, 2},
					{-1, -2},
					{2, 1},
					{2, -1},
					{-2, 1},
					{-2, -1},
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				return coords;
			}
		};
		movesPerPiece.put((int) 'H', adapter);
		movesPerPiece.put((int) 'h', adapter);

		// BISHOP
		adapter = new Move() {
			private int [][] coords = {
					{1, 1},
					{2, 2},
					{3, 3},
					{4, 4},
					{5, 5},
					{6, 6},
					{7, 7},
					{8, 8},
					{1, 1},
					{2, 2},
					{3, 3},
					{4, 4},
					{5, 5},
					{6, 6},
					{7, 7},
					{8, 8},

					{-1, 1},
					{-2, 2},
					{-3, 3},
					{-4, 4},
					{-5, 5},
					{-6, 6},
					{-7, 7},
					{-8, 8},
					{1, -1},
					{2, -2},
					{3, -3},
					{4, -4},
					{5, -5},
					{6, -6},
					{7, -7},
					{8, -8},
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				return coords;
			}
		};
		movesPerPiece.put((int) 'B', adapter);
		movesPerPiece.put((int) 'b', adapter);
		
		// KING
		adapter = new Move() {
			private int [][] coords = {
					{0, 1},
					{0, -1},
					{1, 0},
					{-1, 0},
					
					{1, 1},
					{1, -1},
					{-1, 1},
					{-1, -1},
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				return coords;
			}
		};
		movesPerPiece.put((int) 'K', adapter);
		movesPerPiece.put((int) 'k', adapter);
		
		//QUEEN
		adapter = new Move() {
			private int [][] coords = {
					{1, 1},
					{2, 2},
					{3, 3},
					{4, 4},
					{5, 5},
					{6, 6},
					{7, 7},
					{8, 8},
					{1, 1},
					{2, 2},
					{3, 3},
					{4, 4},
					{5, 5},
					{6, 6},
					{7, 7},
					{8, 8},
					{-1, 1},
					{-2, 2},
					{-3, 3},
					{-4, 4},
					{-5, 5},
					{-6, 6},
					{-7, 7},
					{-8, 8},
					{1, -1},
					{2, -2},
					{3, -3},
					{4, -4},
					{5, -5},
					{6, -6},
					{7, -7},
					{8, -8},
					{0, 1},
					{0, 2},
					{0, 3},
					{0, 4},
					{0, 5},
					{0, 6},
					{0, 7},
					{0, 8},
					{1, 0},
					{2, 0},
					{3, 0},
					{4, 0},
					{5, 0},
					{6, 0},
					{7, 0},
					{8, 0},
					{0, -1},
					{0, -2},
					{0, -3},
					{0, -4},
					{0, -5},
					{0, -6},
					{0, -7},
					{0, -8},
					{-1, 0},
					{-2, 0},
					{-3, 0},
					{-4, 0},
					{-5, 0},
					{-6, 0},
					{-7, 0},
					{-8, 0},
			};
			
			@Override
			protected int[][] getCoordinates(int x, int y, IntTileLayer layer) {
				return coords;
			}
		};
		movesPerPiece.put((int) 'Q', adapter);
		movesPerPiece.put((int) 'q', adapter);
		
	}

	private ChessHelper() {
	}
	
	public static int getPieceType(int in) {
		return Character.toLowerCase(in);
	}
	
	public static boolean isEmpty(int one) {
		return EMPTY == one;
	}
	
	public static boolean isEnemies(int one, int two) {
		if (isPiece(one) && isPiece(two)) {
			return Character.isUpperCase(one) != Character.isUpperCase(two);
		}
		return false;
	}
	
	public static boolean isPiece(int piece) {
		return movesPerPiece.containsKey(piece);
	}
	
	public static boolean isPawn(int c) {
		return PAWN == Character.toLowerCase(c);
	}
	
	public static boolean isTower(int c) {
		return TOWER == Character.toLowerCase(c);
	}
	
	public static boolean isKnight(int c) {
		return KNIGHT == Character.toLowerCase(c);
	}
	
	public static boolean isBishop(int c) {
		return BISHOP == Character.toLowerCase(c);
	}
	
	public static boolean isKing(int c) {
		return KING == Character.toLowerCase(c);
	}
	
	public static boolean isQueen(int c) {
		return QUEEN == Character.toLowerCase(c);
	}
	
	public static IntTileLayer getInitialLayer() {
		return getInitialLayer(null);
	}
	
	public static IntTileLayer getInitialLayer(IntTileLayer layer) {
		layer = layer != null ? layer : new IntTileLayer(8, 8);

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				layer.set(i, j, EMPTY);
			}
		}
		
		for (int i = 0; i < 8; i++) {
			layer.set(i, 1, Character.toUpperCase(PAWN));
			layer.set(i, 6, Character.toLowerCase(PAWN));
		}

		int i = 0;
		layer.set(i, 0, Character.toUpperCase(TOWER));
		layer.set(i++, 7, Character.toLowerCase(TOWER));

		layer.set(i, 0, Character.toUpperCase(KNIGHT));
		layer.set(i++, 7, Character.toLowerCase(KNIGHT));

		layer.set(i, 0, Character.toUpperCase(BISHOP));
		layer.set(i++, 7, Character.toLowerCase(BISHOP));

		layer.set(i, 0, Character.toUpperCase(QUEEN));
		layer.set(i++, 7, Character.toLowerCase(KING));

		layer.set(i, 0, Character.toUpperCase(KING));
		layer.set(i++, 7, Character.toLowerCase(QUEEN));

		layer.set(i, 0, Character.toUpperCase(BISHOP));
		layer.set(i++, 7, Character.toLowerCase(BISHOP));

		layer.set(i, 0, Character.toUpperCase(KNIGHT));
		layer.set(i++, 7, Character.toLowerCase(KNIGHT));

		layer.set(i, 0, Character.toUpperCase(TOWER));
		layer.set(i++, 7, Character.toLowerCase(TOWER));
		
		return layer;
	}

	public static boolean canMove(int x, int y, IntTileLayer layer) {
		int piece = layer.get(x, y);
		Move move = movesPerPiece.get(piece);
		if (move != null) {
			return move.canMove(x, y, layer);
		}
		return false;
	}
	
	public static List<ChessSelection> getCanMoves(IntTileLayer layer, boolean caps) {
		List<ChessSelection> ret = new ArrayList<ChessSelection>();
		if (ret.isEmpty()) {
			if (caps) {
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						int c = layer.get(x, y);
						if (isPiece(c) && isCap(c)) {
							if (canMove(x, y, layer)) {
								ret.add(createSelection(null, layer.get(x, y), x, y, 1));
							}
						}
					}
				}
			} else {
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						int c = layer.get(x, y);
						if (isPiece(c) && !isCap(c)) {
							if (canMove(x, y, layer)) {
								ret.add(createSelection(null, layer.get(x, y), x, y, 1));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	public static boolean isCap(Integer piece) {
		return isPiece(piece) && Character.isUpperCase(piece);
	}

	public static List<ChessSelection> getCanMoveTos(int srcx, int srcy, IntTileLayer layer, boolean runChecks) {
		List<ChessSelection> ret = Collections.emptyList();
		int piece = layer.get(srcx, srcy);
		Move move = movesPerPiece.get(piece);
		if (move != null) {
			ret = move.getMoves(srcx, srcy, layer);
		}
		if (runChecks && isKing(piece)) {
			filterMateMoves(ret, layer, isCap(piece));
		}
		return ret;
	}
	
	private static void filterMateMoves(List<ChessSelection> ret,
			IntTileLayer layer, boolean caps) {
		for (ChessSelection selection : getCanMoves(layer, !caps)) {
			for (ChessSelection to : getCanMoveTos(selection.getX(), selection.getY(), layer, false)) {
				ChessSelection found = null;
				for (ChessSelection current : ret) {
					if (current.getX().equals(to.getX()) && current.getY().equals(to.getY())) {
						found = to;
						break;
					}
				}
				ret.remove(found);
			}
		}
		
	}

	public static int getCheckLevel(IntTileLayer layer, boolean caps, List<ChessSelection> kingSaveSelections) {
		int ret = CHECK_LEVEL_NONE;
		int kx = 0;
		int ky = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int c = layer.get(x, y);
				if (isKing(c)) {
					if (caps && Character.isUpperCase(c)) {
						kx = x;
						ky = y;
						break;
					} else if(!caps && !Character.isUpperCase(c)) {
						kx = x;
						ky = y;
						break;
					}
				}
			}
		}
		// Allegedly we've got a king
		//run all the oppossing and see if they can kill the man
		Map<ChessSelection, ChessSelection> mates = new HashMap<ChessSelection, ChessSelection>();
		for (ChessSelection selection : getCanMoves(layer, !caps)) {
			for (ChessSelection to : getCanMoveTos(selection.getX(), selection.getY(), layer, false)) {
				if (to.getX() == kx && to.getY() == ky) {
					ret = CHECK_LEVEL_CHECK;
					mates.put(selection, to);
				}
			}
		}
		if (ret == CHECK_LEVEL_CHECK) {
			// If we've gotta check... see if king can escape or other piece move to block
			// All of the mates must be block by just one movement.
			//Let's suppose checkmate!
			ret = CHECK_LEVEL_MATE;
			for (ChessSelection selection : getCanMoves(layer, caps)) {
				for (ChessSelection to : getCanMoveTos(selection.getX(), selection.getY(), layer, false)) {
					int lastChar = layer.get(to.getX(), to.getY());
					int currChar = layer.get(selection.getX(), selection.getY());
					layer.set(selection.getX(), selection.getY(), EMPTY);
					layer.set(to.getX(), to.getY(), currChar);
					boolean allMatesKilled = true;
					for (ChessSelection mate : mates.keySet()) {
						ChessSelection mateTo = mates.get(mate);
						if (movesPerPiece.get(currChar).canMoveTo(mate.getX(), mate.getY(), mateTo.getX(), mateTo.getY(), layer)) {
							if (kingSaveSelections != null) {
								
							}
							allMatesKilled = false;
						}
					}
					//Undo changes
					layer.set(to.getX(), to.getY(), lastChar);
					layer.set(selection.getX(), selection.getY(), currChar);
					if (allMatesKilled) {
						//player can escape from the check position... Hence, we can break, we break if we don´t need to fill up the save movements
						ret = CHECK_LEVEL_CHECK;
						if (kingSaveSelections != null) {
							
						} else {
							break;
						}
					}
				}
				if (ret == CHECK_LEVEL_CHECK) {
					if (kingSaveSelections != null) {
						//do something?
					} else {
						break;
					}
				}
			}
		}
		return ret;
	}
	
	public static ChessSelection createSelection(String playerName, long tile, Integer x, Integer y, Integer z) {
		return createSelection(playerName, tile, x, y, z, false);
	}
	
	public static ChessSelection createSelection(String playerName, long tile, Integer x, Integer y, Integer z, boolean isJump) {
		ChessSelection selection = new ChessSelection();
		selection.setPlayerName(playerName);
		selection.setLayer(1);
		selection.setModelId((long) tile);
		selection.setX(x);
		selection.setY(y);
		selection.setLayer(z);
		return selection;
	}

}
