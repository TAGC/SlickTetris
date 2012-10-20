package game;

public class Pit {
	
	public static final int PIT_WIDTH = 10;
	public static final int PIT_DEPTH = 20;
	private boolean[][] pitOccupiedSpaces;
	
	public Pit() {
		pitOccupiedSpaces = new boolean[PIT_WIDTH][PIT_DEPTH];
	}
	
	public boolean isSpaceOccupied(int x, int y) {
		if (x < 0 || x >= PIT_WIDTH || y >= PIT_DEPTH) return true;
		return pitOccupiedSpaces[x][y];
	}
	
	public boolean[][] getOccupiedSpaces() {
		return pitOccupiedSpaces;
	}
}
