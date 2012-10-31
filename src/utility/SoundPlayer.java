package utility;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class SoundPlayer {
	
	private String[] sounds;
	private Sound[] loadedSounds;
	private Map<String, Sound> soundMap;
	
	public SoundPlayer(String[] sounds, String soundsPath) {
		this.sounds = sounds;
		
		loadedSounds = new Sound[sounds.length];
		soundMap = new HashMap<String, Sound>();
		
		for(int i=0; i < sounds.length; i++) {
			try {
				loadedSounds[i] = new Sound(soundsPath + sounds[i]);
				soundMap.put(sounds[i], loadedSounds[i]);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String[] getSounds() {
		return sounds;
	}
	
	public void playSound(String soundName) {
		Sound sound;
		
		if(soundMap.containsKey(soundName)) {
			sound = soundMap.get(soundName);
			sound.play();
			if(!sound.playing()) {
				System.out.println("Sound failed to play");
			}
		} else {
			System.out.println("Sound <" + soundName + "> does not exist");
		}
	}
}
