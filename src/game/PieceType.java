package game;

public enum PieceType {
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

	private boolean[][] occupiedSpaces;
	
	PieceType(boolean[][] occupiedSpaces) {
		this.occupiedSpaces = occupiedSpaces;
	}
	
	public boolean[][] getOccupiedSpaces() {
		return occupiedSpaces;
	}
}
