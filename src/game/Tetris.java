package game;

import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Tetris extends BasicGame {
	
	public final static int WINDOW_WIDTH = 475;
	public final static int WINDOW_HEIGHT = 650;
	private Pit pit;
	private Piece activePiece;
	
	private final static int PIECE_FALL_TIMER_MAX = 500;
	private final static int MOVE_COOLDOWN_MAX = 150;
	private final static int ROTATE_COOLDOWN_MAX = 150;
	private int pieceFallTimer = 0;
	private int moveCooldown = 0;
	private int rotateCooldown = 0;
	
	private boolean setRotate = false;
	private Direction nextMove = null;
	
	public Tetris() {
		super("SlickTetris");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		activePiece.display();
		pit.display();
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		pit = new Pit();
		setActivePiece();
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		handleInput(container.getInput());
		setActivePiece();
		
		if(moveCooldown < MOVE_COOLDOWN_MAX) {
			moveCooldown += delta;
			System.out.println("nextMove = " + nextMove);
		} else if(nextMove != null) {
			activePiece.movePiece(nextMove);
			moveCooldown = 0;
			nextMove = null;
		}
		
		if(rotateCooldown < ROTATE_COOLDOWN_MAX) {
			//System.out.println("rotateCooldown : " + rotateCooldown);
			//System.out.println("setRotate : " + setRotate);
			rotateCooldown += delta;
		} else if(setRotate) {
			rotateCooldown = 0;
			setRotate = false;
			System.out.println("Rotating in main");
			activePiece.rotate();
		}
		
		pieceFallTimer += delta;
		if(pieceFallTimer >= PIECE_FALL_TIMER_MAX)
		{
			pieceFallTimer = 0;
			if(nextMove != Direction.DOWN) {
				activePiece.movePiece(Direction.DOWN);
			}
		}
	}
	
	private void setActivePiece() {
		if(activePiece == null || !activePiece.getActive()) {
			activePiece = getRandomPiece();
			activePiece.drop(pit);
		}
	}
	
	private void handleInput(Input input) {
		
		if(moveCooldown >= MOVE_COOLDOWN_MAX) {
			if(input.isKeyDown(Input.KEY_A)) {
				nextMove = Direction.LEFT;
			}
			else if(input.isKeyDown(Input.KEY_D)) {
				nextMove = Direction.RIGHT;
			}
			else if(input.isKeyDown(Input.KEY_S)) {
				nextMove = Direction.DOWN;
			}
		}
		
		if(input.isKeyDown(Input.KEY_SPACE)) {
			if(rotateCooldown >= ROTATE_COOLDOWN_MAX) {
				setRotate = true;
			}
		}
	}
	
	private Piece getRandomPiece() {
		Piece[] pieces = Piece.values();
		Random rand = new Random();
		return pieces[rand.nextInt(pieces.length)];
	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Tetris(),
					WINDOW_WIDTH, WINDOW_HEIGHT, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
