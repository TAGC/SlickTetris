package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Tetris extends BasicGame {
	
	private final static int WINDOW_WIDTH = 480;
	private final static int WINDOW_HEIGHT = 640;
	
	public Tetris() {
		super("SlickTetris");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
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
