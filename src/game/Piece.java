package game;

import java.util.Random;

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
	
	private static final int BLOCKS_PER_PIECE = 4; 
	private boolean[][] occupiedSpaces;
	private int[] topLeftSpaceLocation;
	private Pit pit;
	private Block[] blocks;
	private boolean active;
	
	Piece(boolean[][] occupiedSpaces) {
		Random rand;
		Colour[] colours;
		Colour colour;
		
		this.occupiedSpaces = occupiedSpaces;
		topLeftSpaceLocation = new int[2];
		
		rand = new Random();
		colours = Colour.values();
		
		blocks = new Block[4];
		for(int i=0; i < BLOCKS_PER_PIECE; i++) {
			colour = colours[rand.nextInt(colours.length)];
			blocks[i] = new Block(colour);
		}
	}
	
	public void drop(Pit pit) {
		setPitState(pit);
		System.out.println("Shape height : " + getHeight() + ", shape width : " + getWidth());
		setTopLeftSpaceLocation(Pit.PIT_WIDTH/2 - getWidth()/2, 0);
		setActive(true);
	}
	
	private void setPitState(Pit pit) {
		this.pit = pit;
	}
	
	private void setOccupiedSpaces(boolean[][] newOccupiedSpaces) {
		occupiedSpaces = newOccupiedSpaces;
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
	
	public boolean getActive() {
		return active;
	}
	
	private void setActive(boolean active) {
		this.active = active;
	}
	
	public void movePiece(Direction direction) {
		int[] posChange = direction.positionChange();
		moveHorizontally(posChange[0]);
		moveVertically(posChange[1]);
	}
	
	// Rotates 90 degrees clockwise.
	public void rotate() {
		boolean[][] newOccupiedSpaces = new boolean[4][4];
		boolean[][] currOccupiedSpaces = getOccupiedSpaces();
		
		for(int x=0; x < 4; x++) {
			for(int y=0; y < 4; y++) {
				
				// Top-left quadrant.
				if(x <= 1 && y <= 1) {
					newOccupiedSpaces[3-x][y] = currOccupiedSpaces[x][y];
				}
				// Top-right quadrant.
				else if(x >= 2 && y <= 1) {
					newOccupiedSpaces[x][3-y] = currOccupiedSpaces[x][y];
				}
				// Bottom-right quadrant.
				else if(x >= 2 && y >= 2) {
					newOccupiedSpaces[x-2][y] = currOccupiedSpaces[x][y];
					
				}
				// Bottom-left quadrant.
				else if(x <= 1 && y >= 2) {
					newOccupiedSpaces[x][y-2] = currOccupiedSpaces[x][y];
				}
			}
			setOccupiedSpaces(newOccupiedSpaces);
		}
	}
	
	private void embedBlocksInPit() {
		int absX = getTopLeftSpaceLocation()[0];
		int absY = getTopLeftSpaceLocation()[1];
		int blockX, blockY;
		
		int blockIndex = 0;
		for(int x=0; x < BLOCKS_PER_PIECE; x++) {
			for(int y=0; y < BLOCKS_PER_PIECE; y++) {
				if(isSpaceOccupiedBySelf(x, y)) {
					blockX = x + absX;
					blockY = y + absY;
					
					pit.addBlockToPit(blocks[blockIndex], blockX, blockY);
					blockIndex++;
				}
			}
		}
	}
	
	private int getHeight() {
		for(int y=3; y >= 0; y--) {
			for(int x=0; x < 4; x++) {
				if(isSpaceOccupiedBySelf(x, y)) {
					return 3-y;
				}
			}
		}
		
		return 0;
	}
	
	private int getWidth() {
		for(int x=3; x >= 0; x--) {
			for(int y=0; y < 4; y++) {
				if(isSpaceOccupiedBySelf(x, y)) {
					return 3-x;
				}
			}
		}
		
		return 0;
	}
	
	// Iterating over columns and rows (bottom-up), if each piece-block
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
				
				// If the bottom of the column is occupied by block of this
				// piece, and the spot beneath that is occupied by block
				// of another piece (or the floor), the column  is not
				// movable.
				if(y == 3 && isSpaceOccupiedBySelf(x, y)
						&& pit.isSpaceOccupied(absX, absY+1)) {
					setActive(false);
					embedBlocksInPit();
					return;
				}
				
				// If block of another piece (or the floor) is blocking the
				// spot below the current spot, the column is not movable.
				//
				// If the spot beneath is empty, or blocked by another block
				// of the same piece, then the column may still be movable.
				else if(isSpaceOccupiedBySelf(x, y)
						&& !isSpaceOccupiedBySelf(x, y+1) 
						&& pit.isSpaceOccupied(absX, absY+1)) {
					setActive(false);
					embedBlocksInPit();
					return;
				}
			}
		}
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		setTopLeftSpaceLocation(absX, absY+1);
	}
	
	// Iterating over rows and columns (leftwards-in/rightwards-in), if each
	// piece-block in any given row can move (leftwards/rightwards), then the
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
				// spot of a given row is occupied by block of this piece,
				// then the row cannot move sideways as a whole if the
				// spot right of it is occupied by block of another
				// piece (or the wall).
				if(posChange == 1 && x == 3
						&& isSpaceOccupiedBySelf(x, y)
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
				// If block of another piece (or the wall) is blocking the
				// spot in the intended direction of motion of the current
				// spot, the column is not movable.
				else if(isSpaceOccupiedBySelf(x, y)
						&& !isSpaceOccupiedBySelf(x+posChange, y)
						&& pit.isSpaceOccupied(absX+posChange, absY)) {
					return;
				}
			}
		}
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		setTopLeftSpaceLocation(absX+posChange, absY);
	}
	
	public void display() {	
		int pitX = pit.getTopLeftPixelLocation()[0];
		int pitY = pit.getTopLeftPixelLocation()[1];
		
		int pieceX = this.getTopLeftSpaceLocation()[0]*Pit.SPACE_WIDTH + pitX;
		int pieceY = this.getTopLeftSpaceLocation()[1]*Pit.SPACE_HEIGHT + pitY;
		
		int blockX, blockY;
		
		int index = 0;
		while(index < BLOCKS_PER_PIECE) {
			for(int x=0; x < 4; x++) {
				for(int y=0; y < 4; y++) {
					
					if(isSpaceOccupiedBySelf(x, y)) {
						blockX = x*Pit.SPACE_WIDTH + pieceX;
						blockY = y*Pit.SPACE_HEIGHT + pieceY;
						
						blocks[index].display(blockX, blockY);
						index++;
					}
				}
			}
		}
	}
}

