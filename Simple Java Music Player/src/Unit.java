
public class Unit {
	
	Unit next;
	Unit prev;
	
	public Unit(){
		next = prev = null;
	}
	
	public int numSongs() {
		return 0;
	}
	
	public Unit addSong(Song s) {
		return new SingleSongUnit(s);
	}
	
	public Unit removeSong(Song s) {
		return null;
	}
	
	public void play(){
		System.out.println("Playing will happen here...");
	}
	
	
}
