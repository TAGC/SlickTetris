package game;

import org.newdawn.slick.Image;

public class Block {
	private Image colourImage;
	
	public Block(Colour colour) {
		colourImage = colour.getImage();
	}
	
	public void display(int x, int y) {
		colourImage.draw(x, y);
	}
}
