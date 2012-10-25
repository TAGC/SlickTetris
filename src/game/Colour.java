package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum Colour {
	BLUE ("resources\\images\\blue_piece_colour.png"),
	GREEN ("resources\\images\\green_piece_colour.png"),
	RED ("resources\\images\\red_piece_colour.png"),
	YELLOW ("resources\\images\\yellow_piece_colour.png");
	
	private final String path;
	
	Colour(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public Image getImage() {
		try {
			return new Image(getPath());
		} catch (SlickException e) {
			e.printStackTrace();
			return null;
		}
	}
}
