package game;

public enum Direction {
	RIGHT (1, 0),
	DOWN (0, -1),
	LEFT (-1, 0);
	
	private final int verticalMotion;
	private final int horizontalMotion;
	
	Direction(int verticalMotion, int horizontalMotion) {
		this.verticalMotion = verticalMotion;
		this.horizontalMotion = horizontalMotion;
	}
	
	public int[] positionChange() {
		int[] posChange = new int[]{ verticalMotion, horizontalMotion };
		return posChange;
	}
	
}
