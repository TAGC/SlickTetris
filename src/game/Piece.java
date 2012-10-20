package game;

public enum Piece {
	O_PIECE (new boolean[][]{{true, true, false, false},
							 {true, true, false, false},
							 {false, false, false, false},
							 {false, false, false, false}
							 }),
	J_PIECE (new boolean[][]{{false, true, false, false},
							 {false, true, false, false},
							 {true, true, false, false},
							 {false, false, false, false}
							 }),
	I_PIECE (new boolean[][]{{true, false, false, false},
							 {true, false, false, false},
							 {true, false, false, false},
							 {true, false, false, false}
							 }),
	L_PIECE (new boolean[][]{{true, false, false, false},
							 {true, false, false, false},
							 {true, true, false, false},
							 {false, false, false, false}
							 }),
	S_PIECE (new boolean[][]{{false, true, true, false},
							 {true, true, false, false},
							 {false, false, false, false},
							 {false, false, false, false}
							 }),
	Z_PIECE (new boolean[][]{{true, true, false, false},
							 {false, true, true, false},
							 {false, false, false, false},
							 {false, false, false, false}
							 }),
	T_PIECE (new boolean[][]{{true, true, true, false},
							 {false, true, false, false},
							 {false, false, false, false},
							 {false, false, false, false}
							 });
	
	private final boolean[][] occupiedSpaces;
	private int[] topLeftSpaceLocation;
	private Pit pit;
	
	Piece(boolean[][] occupiedSpaces) {
		this.occupiedSpaces = occupiedSpaces;
		topLeftSpaceLocation = new int[2];
	}
	
	public void setPitState(Pit pit) {
		this.pit = pit;
	}
	
	public boolean[][] getOccupiedSpaces() {
		return occupiedSpaces;
	}
	
	public boolean isSpaceOccupiedBySelf(int x, int y) {
		if (x < 0 || x >= 4 || y < 0 || y >= 4) return false;
		return getOccupiedSpaces()[y][x];
	}
	
	public int[] getTopLeftSpaceLocation() {
		return topLeftSpaceLocation;
	}
	
	public void setTopLeftSpaceLocation(int x, int y) {
		topLeftSpaceLocation = new int[]{ x, y };
	}
	
	public void movePiece(Direction direction) {
		int[] posChange = direction.positionChange();
		moveHorizontally(posChange[0]);
		moveVertically(posChange[1]);
	}
	
	private int getHeight() {
		for(int y=0; y < 4; y++) {
			for(int x=0; x < 4; x++) {
				if(isSpaceOccupiedBySelf(x, y)) {
					return 4-y;
				}
			}
		}
		
		return 0;
	}
	
	private int getWidth() {
		for(int x=3; x >= 0; x--) {
			for(int y=0; y < 4; y++) {
				if(isSpaceOccupiedBySelf(x, y)) {
					return 4-x;
				}
			}
		}
		
		return 0;
	}
	
	// Iterating over columns and rows (bottom-up), if each piece-part
	// in a given column can move downwards then that column as a whole
	// can move downwards. If all columns can move downwards, then the
	// piece can move downwards as a whole.
	private void moveVertically(int posChange) {
		int absX, absY;
		
		if(posChange == 0) return;
		
		// Iterate over the columns.
		for(int x=0; x < 4; x++) {
			// Iterate over each spot in the column.
			for(int y=0; y < 4; y++) {
				absX = getTopLeftSpaceLocation()[0] + x;
				absY = getTopLeftSpaceLocation()[1] + y;
				
				// If the bottom of the column is occupied by part of this
				// piece, and the spot beneath that is occupied by part
				// of another piece (or the floor), the column  is not
				// movable.
				if(y == 3 && isSpaceOccupiedBySelf(x, y)
						&& pit.isSpaceOccupied(absX, absY-1)) {
					return;
				}
				
				// If part of another piece (or the floor) is blocking the
				// spot below the current spot, the column is not movable.
				//
				// If the spot beneath is empty, or blocked by another part
				// of the same piece, then the column may still be movable.
				else if(!isSpaceOccupiedBySelf(x, y-1) 
						&& pit.isSpaceOccupied(absX, absY-1)) {
					return;
				}
			}
		}
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		setTopLeftSpaceLocation(absX, absY-1);
	}
	
	// Iterating over rows and columns (leftwards-in/rightwards-in), if each
	// piece-part in any given row can move (leftwards/rightwards), then the
	// row can move sideways. If all rows can move sideways, then the piece
	// can move sideways as a whole.
	private void moveHorizontally(int posChange) {
		int absX, absY;
		
		if(posChange == 0) return;
		
		// Iterate over rows.
		for(int y=0; y < 4; y++) {
			// Iterate over each spot in the row.
			for(int x=0; x < 4; x++) {
				absX = getTopLeftSpaceLocation()[0] + x;
				absY = getTopLeftSpaceLocation()[1] + y;
				
				// If the piece is trying to move right and the right-most
				// spot of a given row is occupied by part of this piece,
				// then the row cannot move sideways as a whole if the
				// spot right of it is occupied by part of another
				// piece (or the wall).
				if(posChange == 1 && x == 3 && isSpaceOccupiedBySelf(x, y)
						&& pit.isSpaceOccupied(absX+1, absY)) {
					return;
				}
				// Test again for the leftwards-direction if intended motion
				// is leftwards.
				else if(posChange == -1 && x == 0
						&& isSpaceOccupiedBySelf(x, y)
						&& pit.isSpaceOccupied(absX-1, absY)) {
					return;
				}
				// If part of another piece (or the wall) is blocking the
				// spot in the intended direction of motion of the current
				// spot, the column is not movable.
				//
				// Testing right:
				else if(posChange == 1 && !isSpaceOccupiedBySelf(x+1, y)
						&& pit.isSpaceOccupied(absX+1, absY)) {
					return;
				}
				// Testing left:
				else if(posChange == -1 && !isSpaceOccupiedBySelf(x-1, y)
						&& pit.isSpaceOccupied(absX-1, absY)) {
					return;
				}
				
			}
		}
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		setTopLeftSpaceLocation(absX+posChange, absY);
	}
}

