package game;

import java.util.Random;

import utility.SoundPlayer;

public class Piece {
	
	private static final int BLOCKS_PER_PIECE = 4;
	private final boolean[][] occupiedSpacesFinal;
	private boolean[][] occupiedSpaces;
	private int[] topLeftSpaceLocation;
	private Pit pit;
	private Block[] blocks;
	private boolean active;
	private int rotationState = 0;
	
	Piece(PieceType pieceType) {
		Random rand;
		Colour[] colours;
		Colour colour;
		
		this.occupiedSpaces = pieceType.getOccupiedSpaces();
		occupiedSpacesFinal = occupiedSpaces;
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
		setTopLeftSpaceLocation(Pit.PIT_WIDTH/2 - getWidth()/2, 0);
		
		if(!isValidPosition()) {
			pit.setOverflowStatus(true);
		} else {
			setActive(true);
		}
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
	
	private boolean[][] getOccupiedSpacesFinal() {
		return occupiedSpacesFinal;
	}
	
	public boolean isSpaceOccupiedBySelf(int x, int y) {
		if (x < 0 || x >= 4 || y < 0 || y >= 4) return false;
		return getOccupiedSpaces()[y][x];
	}
	
	private boolean isSpaceOccupiedBySelfFinal(int x, int y) {
		if (x < 0 || x >= 4 || y < 0 || y >= 4) return false;
		return getOccupiedSpacesFinal()[y][x];
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
	
	// Rotates 90 degrees anti-clockwise.
	public void rotate() {
		boolean[][] newOccupiedSpaces = new boolean[4][4];
		boolean[][] currOccupiedSpaces = getOccupiedSpaces();
		double originX, originY, newX, newY;
		int absX, absY, width, height;
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		width = getWidth();
		height = getHeight();
		
		switch(rotationState) {
		case 0:
			setTopLeftSpaceLocation(absX - (4 - width), absY);
			break;
			
		case 1:
			setTopLeftSpaceLocation(absX, absY - (4 - height));
			break;
			
		case 2:
			setTopLeftSpaceLocation(absX + (4 - width), absY);
			break;
			
		case 3:
			setTopLeftSpaceLocation(absX, absY + (4 - height));
			break;
			
		default:
			System.out.println("Error occurred in rotation");
			break;
		}
		
		for(int x=0; x < 4; x++) {
			for(int y=0; y < 4; y++) {
				originX = x - 1.5;
				originY = y - 1.5;
				newX = originY + 1.5;
				newY = -originX + 1.5;
				newOccupiedSpaces[(int)newX][(int)newY] = currOccupiedSpaces[x][y];
			}
		}
		
		setOccupiedSpaces(newOccupiedSpaces);
		if(isValidPosition()) {
			rotationState = (rotationState + 1) % 4;
		} else {
			setOccupiedSpaces(currOccupiedSpaces);
			setTopLeftSpaceLocation(absX, absY);
		}
	}
	
	private boolean isValidPosition() {
		int absX, absY;
		int blockX, blockY;
		
		absX = getTopLeftSpaceLocation()[0];
		absY = getTopLeftSpaceLocation()[1];
		
		for(int x=0; x < 4; x++) {
			for(int y=0; y < 4; y++) {
				
				if(isSpaceOccupiedBySelf(x, y)) {
					blockX = absX + x;
					blockY = absY + y;
					
					if(pit.isSpaceOccupied(blockX, blockY)) return false;
				}
			}
		}
		
		return true;
	}
	
	private void embedBlocksInPit() {
		SoundPlayer pitSoundPlayer;
		
		pitSoundPlayer = pit.getSoundPlayer();
		String pieceEmbedSound = "blip.ogg";
		
		int absX = getTopLeftSpaceLocation()[0];
		int absY = getTopLeftSpaceLocation()[1];
		int blockX, blockY, x, y;
		
		int blockIndex = 0;
		for(int trueY=0; trueY < 4; trueY++) {
			for(int trueX=0; trueX < 4; trueX++) {
				
				x = changeIterationThroughArray(trueX, trueY)[0];
				y = changeIterationThroughArray(trueX, trueY)[1];
				
				if(isSpaceOccupiedBySelf(x, y)) {
					blockX = x + absX;
					blockY = y + absY;
					
					pit.addBlockToPit(blocks[blockIndex], blockX, blockY);
					pit.addPoints(blocks[blockIndex].getColour().getWorth());
					blockIndex++;
				}
			}
		}
		
		pitSoundPlayer.playSound(pieceEmbedSound);
		pit.deleteFullRows();
	}
	
	private int getHeight() {
		for(int y=3; y >= 0; y--) {
			for(int x=0; x < 4; x++) {
				if(isSpaceOccupiedBySelfFinal(x, y)) {
					return 3-y;
				}
			}
		}
		
		return 0;
	}
	
	private int getWidth() {
		for(int x=3; x >= 0; x--) {
			for(int y=0; y < 4; y++) {
				if(isSpaceOccupiedBySelfFinal(x, y)) {
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
	
	private int[] changeIterationThroughArray(int trueX, int trueY) {
		int x, y;
		
		switch(rotationState) {
		// Normal orientation, go right and then down.
		case 0:
			x = trueX;
			y = trueY;
			break;
			
		// Rotated 90 degrees clockwise, go down and
		// then leftwards.
		case 1:
			x = 3-trueY;
			y = trueX;
			break;
			
		// Rotated 180 degrees clockwise, go left and
		// then up.
		case 2:
			x = 3-trueX;
			y = 3-trueY;
			break;
			
		// Rotated 270 degrees clockwise, go up and
		// then right.
		case 3:
			x = trueY;
			y = 3-trueX;
			break;
			
		default:
			System.out.println("Error occurred in changing" +
					"iteration through array");
			return null;
		}
		
		return new int[]{x, y};
	}
	
	public void display() {	
		int pitX = pit.getTopLeftPixelLocation()[0];
		int pitY = pit.getTopLeftPixelLocation()[1];
		
		int pieceX = this.getTopLeftSpaceLocation()[0]*Pit.SPACE_WIDTH + pitX;
		int pieceY = this.getTopLeftSpaceLocation()[1]*Pit.SPACE_HEIGHT + pitY;
		
		int blockX, blockY;
		int x, y;
		
		int index = 0;
		while(index < BLOCKS_PER_PIECE) {
			for(int trueY=0; trueY < 4; trueY++) {
				for(int trueX=0; trueX < 4; trueX++) {
					
					// Keeps the block colouring correct between
					// different states of rotation.
					x = changeIterationThroughArray(trueX, trueY)[0];
					y = changeIterationThroughArray(trueX, trueY)[1];
					
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

