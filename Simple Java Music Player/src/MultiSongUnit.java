import java.util.ArrayList;

/*
 * Unit with multiple songs
 * 
 * 
 */
public class MultiSongUnit extends Unit {
	
	private ArrayList<Song> songs;
	
	public MultiSongUnit(){
		super();
		songs = new ArrayList<Song>();
	}
	
	public MultiSongUnit(ArrayList<Song> l) {
		super();
		songs = l;
	}
	
	public int numSongs() {
		return songs.size();
	}
	
	public ArrayList<Song> getSongs() {
		return songs;
	}

	public Unit addSong(Song s) {
		songs.add(s);
		return this;
	}

	public Unit removeSong(Song s) {
		songs.remove(s);
		if(numSongs() == 1) {
			return new SingleSongUnit(songs.get(0));
		} else {
			return this;
		}
	}
	
	public void play() {
		System.out.println("Playing a multiple songs will happen here...");
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} 
		
		if(other == null) {
			return false;
		}
		
		if(!(other instanceof MultiSongUnit)){
			return false;
		}
		
		MultiSongUnit o = (MultiSongUnit) other;
		
		return this.songs.equals(o.getSongs());
	}
	
	
	
	
}
