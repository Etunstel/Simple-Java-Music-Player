// Defines a component of a Playlist, currently implemented by SingleSongUnit and MultiSongUnit
public interface Unit {

	public int numSongs();
	
	public Unit addSong(Song s);
	
	public Unit removeSong(Song s);
	
	public UnitType getUnitType();
	
}
