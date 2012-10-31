package utility;

import java.awt.Color;
import java.awt.Font;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class SimpleFont {
	private UnicodeFont font;
	
	public SimpleFont(String fontName, int style, int size, Color colour) {
		this(new Font(fontName, style, size), colour);
	}
	
	public SimpleFont(String fontName, int style, int size) {
		this(new Font(fontName, style, size));
	}
	
	public SimpleFont(Font font) {
		this(font, Color.WHITE);
	}

	@SuppressWarnings("unchecked")
	public SimpleFont(Font font, Color colour) {
		ColorEffect colourEffect;
		
		this.font = new UnicodeFont(font);
		colourEffect = new ColorEffect(colour);
		this.font.getEffects().add(colourEffect);
		this.font.addNeheGlyphs();
		try {
			this.font.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setColour(Color colour) {
		font.getEffects().clear();
		font.getEffects().add(new ColorEffect(colour));
		font.clearGlyphs();
		font.addNeheGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public UnicodeFont get() {
		return font;
	}
	
	

	
}
