package game;

public class Pit {
	
	public static final int SPACE_WIDTH = 25;
	public static final int SPACE_HEIGHT = 25;
	public static final int PIT_WIDTH = Tetris.WINDOW_WIDTH/SPACE_WIDTH;
	public static final int PIT_DEPTH = Tetris.WINDOW_HEIGHT/SPACE_HEIGHT;
	
	private Block[][] pitBlockSpaces;
	private int[] topLeftPixelLocation;
	
	public Pit() {
		pitBlockSpaces = new Block[PIT_WIDTH][PIT_DEPTH];
		
		for(int x=0; x < PIT_WIDTH; x++) {
			for(int y=0; y < PIT_DEPTH; y++) {
				pitBlockSpaces[x][y] = null;
			}
		}
		
		setTopLeftPixelLocation(0, 0);
	}
	
	public boolean isSpaceOccupied(int x, int y) {
		if (x < 0 || x >= PIT_WIDTH || y >= PIT_DEPTH) return true;
		return pitBlockSpaces[x][y] != null;
	}
	
	public Block getBlockAtLocation(int x, int y) {
		return getBlocks()[x][y];
	}
	
	public Block[][] getBlocks() {
		return pitBlockSpaces;
	}
	
	public void addBlockToPit(Block block, int x, int y) {
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
