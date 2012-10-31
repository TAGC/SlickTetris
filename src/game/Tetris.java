package game;

import java.awt.Font;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import utility.MusicPlayer;
import utility.SimpleFont;
import utility.SoundPlayer;

public class Tetris extends BasicGame {
	
	public final static int WINDOW_WIDTH = 475;
	public final static int WINDOW_HEIGHT = 650;
	private Pit pit;
	private Piece activePiece;
	private boolean gamePaused = false;
	
	private final static int PIECE_FALL_TIMER_MAX = 300;
	private final static int MOVE_COOLDOWN_MAX = 100;
	private final static int ROTATE_COOLDOWN_MAX = 150;
	private final static int GENERIC_KEY_COOLDOWN_MAX = 150;
	private int pieceFallTimer = 0;
	private int moveCooldown = 0;
	private int rotateCooldown = 0;
	private int genericKeyCooldown = 0;
	
	private boolean setRotate = false;
	private boolean togglePause  = false;
	private boolean toggleChangeTrack = false;
	private Direction nextMove = null;
	
	private MusicPlayer musicPlayer;
	private final static String songsPath = "resources\\music\\";
	private String[] songs;
	
	private SoundPlayer soundPlayer;
	private final static String soundsPath = "resources\\sounds\\";
	private String[] sounds;
	
	private SimpleFont verdana;
	
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
		renderCenteredMessage("Score : " + pit.getScore(), 150, g);
		
		g.setFont(verdana.get());
		
		renderCenteredMessage("Music : " + musicPlayer.getCurrentSongName(),
				200, g);
		
		if(isGamePaused()) {
			renderCenteredMessage("P A U S E D", 250, g);
		}
	}
	
	/*
	 *  Renders a message at the centre of the game window.
	 *  
	 *  @Param message - the message to be rendered on screen.
	 *  @Param y       - the vertical ordinate that the message
	 *                   should be displayed at.
	 *  @Param g       - the graphics context to render within.
	 */       
	private void renderCenteredMessage(String message, int y, Graphics g) {
		int x;
		int width = g.getFont().getWidth(message);
		
		x = (WINDOW_WIDTH - width)/2;
		g.drawString(message, x, y);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		System.out.println("N E W    G A M E");
		
		verdana = new SimpleFont("Verdana", Font.PLAIN, 10);
		
		initialiseMusic();
		initialiseSoundFX();
		pit = new Pit();
		setActivePiece();
	}
	
	/*
	 *  Initialises the game's music player, which will
	 *  continuously play music as the game continues.
	 */ 
	private void initialiseMusic() {
		songs = new String[]
				{ 
					"Basshunter - DotA (HQ).ogg", 
					"Basshunter - Boten Anna (HQ).ogg",
					"Massive Attack - Teardrop.ogg",
					//"Tetris 8 bit.ogg"
				};
		
		musicPlayer = new MusicPlayer(songs, songsPath);
		musicPlayer.start();
	}
	
	/*
	 * Initialises the game's sound player, which will
	 * play sound effects when appropriate.
	 */
	private void initialiseSoundFX() {
		sounds = new String[]
				{
				};
		
		soundPlayer = new SoundPlayer(sounds, soundsPath);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		handleInput(container.getInput());
		
		// Control the rate at which the game responds to 
		// generic key presses.
		if(genericKeyCooldown < GENERIC_KEY_COOLDOWN_MAX) {
			genericKeyCooldown += delta;
		}
			
		if(togglePause) {
			togglePause = false;
			genericKeyCooldown = 0;
			
			if(isGamePaused()) {
				resumeGame();
			} else {
				pauseGame();
			}
		}
		
		if(toggleChangeTrack) {
			toggleChangeTrack = false;
			genericKeyCooldown = 0;
			changeMusicTrack();
		}
		
		if(!pit.getOverflowedStatus() && !isGamePaused()) {
			musicPlayer.checkLoadNextSong();
			runGame(delta);
		} else if(pit.getOverflowedStatus()) {
			System.out.println("G A M E    O V E R\n\n");
			System.out.println("Score : " + pit.getScore());
			System.out.println("Thanks for playing");
			Display.destroy();
			System.exit(0);
		}
	}
	
	/*
	 *  If there are no pieces currently being controlled by the player,
	 *  this method will create a new piece and drop it into the pit for
	 *  the player to control.
	 */ 
	private void setActivePiece() {
		if(activePiece == null || !activePiece.getActive()) {
			refreshStates();
			activePiece = getRandomPiece();
			//activePiece = Piece.I_PIECE;
			activePiece.drop(pit);
			setRotate = false;
		}
	}
	
	/*
	 * Resets various states and cooldowns relating to the active piece.
	 */ 
	private void refreshStates() {
		rotateCooldown = 0;
		moveCooldown = 0;
		setRotate = false;
		nextMove = null;
	}
	
	/*
	 * Handles player input and sets various flags based on keys pressed.
	 * 
	 * @Param input - the keyboard, mouse and controller input wrapper.
	 */ 
	private void handleInput(Input input) {
		
		if(genericKeyCooldown >= GENERIC_KEY_COOLDOWN_MAX) {
			if(input.isKeyDown(Input.KEY_P)) {
				togglePause = true;
			}
			else if(input.isKeyDown(Input.KEY_C)) {
				toggleChangeTrack = true;
			}
		}
		
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
	
	/*
	 * Returns a piece based on a randomly chosen PieceType.
	 */
	private Piece getRandomPiece() {
		PieceType[] pieces = PieceType.values();
		Random rand = new Random();
		return new Piece(pieces[rand.nextInt(pieces.length)]);
	}
	
	/*
	 * Carries out most of the updating and processing related
	 * to the game.
	 * 
	 * @Param delta - the time it took in milliseconds it took
	 *                to render the last frame.
	 */
	private void runGame(int delta) {
		setActivePiece();
		
		if(moveCooldown < MOVE_COOLDOWN_MAX) {
			moveCooldown += delta;
		}
		if(nextMove != null) {
			activePiece.movePiece(nextMove);
			if(nextMove == Direction.DOWN) {
				moveCooldown = PIECE_FALL_TIMER_MAX/4;
				pit.addPoints(Pit.DOWN_MOVE_POINTS);
			} else {
				moveCooldown = 0;
			}
			nextMove = null;
		}
		
		if(rotateCooldown < ROTATE_COOLDOWN_MAX) {
			rotateCooldown += delta;
		}
		if(setRotate) {
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
	
	/*
	 * Get the state of the game.
	 * 
	 * @return true if the game is paused, false otherwise.
	 */
	private boolean isGamePaused() {
		return gamePaused;
	}
	
	/*
	 * Changes the currently playing music track.
	 */
	private void changeMusicTrack() {
		musicPlayer.playNewSong();
	}
	
	/*
	 * Pauses the game and the music; has no effect
	 * if the game is already paused.
	 */
	private void pauseGame() {
		if(!isGamePaused()) {
			gamePaused = true;
			musicPlayer.pauseCurrentSong();
		}
	}
	
	/*
	 * Unpauses the game and the music; has no effect
	 * if the game is already running.
	 */
	private void resumeGame() {
		if(isGamePaused()) {
			gamePaused = false;
			musicPlayer.resumeCurrentSong();
			
		}
		
	}
	
	public static String getSoundPath() {
		return soundsPath;
	}
	
	public static String getSongsPath() {
		return songsPath;
	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Tetris(),
					WINDOW_WIDTH, WINDOW_HEIGHT, false);
			
			// Halve global sound volume.
			app.setSoundVolume((float) 0.5);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
