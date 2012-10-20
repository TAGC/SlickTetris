package game;

public enum Direction {
	RIGHT (0, 1),
	DOWN (-1, 0),
	LEFT (0, -1);
	
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
