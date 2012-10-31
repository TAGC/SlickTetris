package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum Colour {
	BLUE ("resources\\images\\blue_piece_colour.png", 10),
	GREEN ("resources\\images\\green_piece_colour.png", 20),
	RED ("resources\\images\\red_piece_colour.png", 50),
	YELLOW ("resources\\images\\yellow_piece_colour.png", 100);
	
	private final String path;
	private final int value;
	
	Colour(String path, int value) {
		this.path = path;
		this.value = value;
	}
	
	public int getWorth() {
		return value;
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
