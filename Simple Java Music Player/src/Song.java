/*
 * Describes essential information for songs. Used for display purposes,
 * and for looking up songs from the music library.
 */
public class Song implements Comparable<Song> {

	private String title;
	private String artist;
	private String album;
	private String duration; 
	
	public Song() {
		this.title = null;
		this.artist = null;
		this.album = null;
		this.duration = null;
	}
	
	
	public String getTitle() {
		return this.title;
	}
	
	public String getArtist() {
		if(title == null ) {
			return "Unknown";
		}
		return artist;
	}
	
	public String getAlbum() {
		if(album == null) {
			return "Unknown";
		}
		return title;
	}
	
	public String getDuration() {
		return this.duration;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} 
		
		if(other == null) {
			return false;
		}
		
		if(!(other instanceof Song)){
			return false;
		}
		
		Song o = (Song) other;
		return (o.getTitle().equals(title) && o.getArtist().equals(artist) && o.getAlbum().equals(album) && o.getDuration().equals(duration));
		
	}
	
	public int compareTo(Song other) {

		return title.compareTo(other.getTitle());
	}
	
	
}
