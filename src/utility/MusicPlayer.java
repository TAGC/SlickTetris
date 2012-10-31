package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class MusicPlayer {
	
	private String[] songs;
	private Music[] loadedSongs;
	private Map<String, Music> musicMap;
	private List<String> playedSongs;
	private String currentSong;
	private Random rand;
	
	private boolean musicPaused = false;
	
	public MusicPlayer(String[] songs, String songsPath) {
		this.songs = songs;
		rand = new Random();
		
		playedSongs = new ArrayList<String>();
		loadedSongs = new Music[songs.length];
		
		musicMap = new HashMap<String, Music>();
		
		for(int i=0; i < songs.length; i++) {
			try {
				loadedSongs[i] = new Music(songsPath + songs[i]);
				musicMap.put(songs[i], loadedSongs[i]);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		selectSong();
		playCurrentSong();
	}
	
	public void selectSong() {
		if(songs.length == 0) return;
		else if(songs.length == playedSongs.size()) {
			playedSongs.clear();
		}
		
		do {
			currentSong = songs[rand.nextInt(songs.length)];
		} while(isSongAlreadyPlayed(currentSong));
		
		playedSongs.add(currentSong);
	}
	
	private boolean isSongAlreadyPlayed(String song) {
		return playedSongs.contains(song);
	}
	
	public void playCurrentSong() {
		if(currentSong == null) return;
		musicMap.get(currentSong).play();
	}
	
	public void pauseCurrentSong() {
		if(currentSong == null) return;
		musicMap.get(currentSong).pause();
		musicPaused = true;
	}
	
	public void resumeCurrentSong() {
		if(currentSong == null) return;
		musicMap.get(currentSong).resume();
		musicPaused = false;
	}
	
	public void playNewSong() {
		selectSong();
		playCurrentSong();
	}
	
	private boolean isMusicPaused() {
		return musicPaused ;
	}
	
	public String getCurrentSongName() {
		if(currentSong == null ) {
			return "No current song";
		}
		
		Pattern p = Pattern.compile(".*[.]");
		Matcher m = p.matcher(currentSong);
		
		if(m.find()) {
			return m.group();
		} else {
			return "<Song name unavailable>";
		}
		
	}
	
	public void checkLoadNextSong() {
		if(musicMap.containsKey(currentSong)
				&& !isMusicPaused()
				&& !musicMap.get(currentSong).playing()) {
			selectSong();
			playCurrentSong();
		}
	}
}
