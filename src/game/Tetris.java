package game;

import java.util.Random;

import org.lwjgl.opengl.Display;
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
		renderCenteredMessage("SlickTetris", 80, g);
		renderCenteredMessage("A work-in-progress by TAGC", 100, g);
	}
	
	private void renderCenteredMessage(String message, int y, Graphics g) {
		int x;
		int width = g.getFont().getWidth(message);
		
		x = (WINDOW_WIDTH - width)/2;
		g.drawString(message, x, y);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		System.out.println("N E W    G A M E");
		pit = new Pit();
		setActivePiece();
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		handleInput(container.getInput());
		if(!pit.getOverflowedStatus()) {
			runGame(delta);
		} else {
			System.out.println("G A M E    O V E R\n\n");
			System.out.println("Thanks for playing");
			Display.destroy();
			System.exit(0);
		}
	}
	
	private void setActivePiece() {
		if(activePiece == null || !activePiece.getActive()) {
			refreshStates();
			activePiece = getRandomPiece();
			//activePiece = Piece.I_PIECE;
			activePiece.drop(pit);
			setRotate = false;
		}
	}
	
	private void refreshStates() {
		rotateCooldown = 0;
		moveCooldown = 0;
		setRotate = false;
		nextMove = null;
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
		PieceType[] pieces = PieceType.values();
		Random rand = new Random();
		return new Piece(pieces[rand.nextInt(pieces.length)]);
	}
	
	private void runGame(int delta) {
		setActivePiece();
		
		if(moveCooldown < MOVE_COOLDOWN_MAX) {
			moveCooldown += delta;
		} else if(nextMove != null) {
			activePiece.movePiece(nextMove);
			moveCooldown = 0;
			nextMove = null;
		}
		
		if(rotateCooldown < ROTATE_COOLDOWN_MAX) {
			rotateCooldown += delta;
		} else if(setRotate) {
			rotateCooldown = 0;
			setRotate = false;
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
