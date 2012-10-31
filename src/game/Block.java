package game;

import org.newdawn.slick.Image;

public class Block {
	private Colour colour;
	private Image colourImage;
	
	public Block(Colour colour) {
		this.colour = colour;
		colourImage = colour.getImage();
	}
	
	public Colour getColour() {
		return colour;
	}
	
	public void display(int x, int y) {
		colourImage.draw(x, y);
	}
}
