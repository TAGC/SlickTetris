package game;

import utility.SoundPlayer;

public class Pit {
	
	public static final int SPACE_WIDTH = 25;
	public static final int SPACE_HEIGHT = 25;
	public static final int PIT_WIDTH = Tetris.WINDOW_WIDTH/SPACE_WIDTH;
	public static final int PIT_DEPTH = Tetris.WINDOW_HEIGHT/SPACE_HEIGHT;
	public static final int DOWN_MOVE_POINTS = 20;
	
	private Block[][] pitBlockSpaces;
	private int[] topLeftPixelLocation;
	private boolean overflowed;
	private int score;
	
	private SoundPlayer soundPlayer;
	
	public Pit() {
		initialisePitSoundFX();
		
		pitBlockSpaces = new Block[PIT_WIDTH][PIT_DEPTH];
		
		for(int x=0; x < PIT_WIDTH; x++) {
			for(int y=0; y < PIT_DEPTH; y++) {
				pitBlockSpaces[x][y] = null;
			}
		}
		
		score = 0;
		overflowed = false;
		setTopLeftPixelLocation(0, 0);
	}
	
	private void initialisePitSoundFX() {
		String[] sounds = new String[]
				{
					"blip.ogg"
				};
		
		soundPlayer = new SoundPlayer(sounds, Tetris.getSoundPath());
	}
	
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}
	
	public boolean isSpaceOccupied(int x, int y) {
		if (x < 0 || x >= PIT_WIDTH || y >= PIT_DEPTH) return true;
		else if(y < 0) return false;
		return pitBlockSpaces[x][y] != null;
	}
	
	public Block getBlockAtLocation(int x, int y) {
		if (x < 0 || x >= PIT_WIDTH || y < 0 || y >= PIT_DEPTH) return null;
		return getBlocks()[x][y];
	}
	
	public Block[][] getBlocks() {
		return pitBlockSpaces;
	}
	
	public void addBlockToPit(Block block, int x, int y) {
		if (x < 0 || x >= PIT_WIDTH || y >= PIT_DEPTH) return;
		pitBlockSpaces[x][y] = block;
	}
	
	public void removeBlockFromPit(int x, int y) {
		pitBlockSpaces[x][y] = null;
	}
	
	public void setTopLeftPixelLocation(int x, int y) {
		topLeftPixelLocation = new int[]{ x, y };
	}
	
	public int[] getTopLeftPixelLocation() {
		return topLeftPixelLocation;
	}
	
	public void setOverflowStatus(boolean overflowed) {
		this.overflowed = overflowed;
	}
	
	public boolean getOverflowedStatus() {
		return overflowed;
	}
	
	public int getScore() {
		return score;
	}
	
	public void addPoints(int points) {
		score += points;
	}
	
	public void deleteFullRows() {
		boolean rowDeleted = false;
		int multiplier = 1;
		int accumScore = 0;
		
		do {
			rowDeleted = false;
			
			for(int y=0; y < PIT_DEPTH; y++) {
				if(isRowFull(y)) {
					accumScore += clearRow(y) * multiplier;
					multiplier++;
					rowDeleted = true;
				}
			}
		} while(rowDeleted);
		
		addPoints(accumScore);
	}
	
	private int clearRow(int row) {
		Block block;
		int clearScore = 0;
		
		for(int x=0; x < PIT_WIDTH; x++) {
			block = getBlockAtLocation(x, row);
			clearScore += block.getColour().getWorth() * 10;
			removeBlockFromPit(x, row);
			
			for(int y=row; y >= 0; y--) {
				block = getBlockAtLocation(x, y-1);
				addBlockToPit(block, x, y);
			}
		}
		
		return clearScore;
	}
	
	private boolean isRowFull(int row) {
		if(row < 0 || row >= PIT_DEPTH) return false;
		
		for(int x=0; x < PIT_WIDTH; x++) {
			if(!isSpaceOccupied(x, row)) return false;
		}
		
		return true;
	}
	
	public void display() {
		int absX = getTopLeftPixelLocation()[0];
		int absY = getTopLeftPixelLocation()[1];
		
		int blockX, blockY;
		
		for(int x=0; x < PIT_WIDTH; x++) {
			for(int y=0; y < PIT_DEPTH; y++) {
				if(isSpaceOccupied(x, y)) {
					
					blockX = x*SPACE_WIDTH + absX;
					blockY = y*SPACE_HEIGHT + absY;
					getBlockAtLocation(x, y).display(blockX, blockY);
				}
			}
		}
	}
}
