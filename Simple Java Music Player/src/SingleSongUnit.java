/*
 * Unit with only one song 
 * 
 */

import java.util.ArrayList;

public class SingleSongUnit implements Unit {

	private Song song; 
	
	public SingleSongUnit(){
		super();
	}
	
	public SingleSongUnit(Song s) {
		super();
		song = s;
	}
	public int numSongs() {
		return 1;
	}
	
	public Song getSong() {
		return song;
	}

	public Unit addSong(Song s) {
		ArrayList<Song> songs = new ArrayList<Song>();
		songs.add(song);
		songs.add(s);
		return new MultiSongUnit(songs);
	}

	public Unit removeSong(Song s) {
		return null;
	}
	
	public UnitType getUnitType(){
		return UnitType.SINGLE;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} 
		
		if(other == null) {
			return false;
		}
		
		if(!(other instanceof SingleSongUnit)){
			return false;
		}
		
		SingleSongUnit o = (SingleSongUnit) other;
		
		return this.song.equals(o.getSong());
	}
	
	public String toString() {
		return "SingleSongUnit: " + song;
	}

}
