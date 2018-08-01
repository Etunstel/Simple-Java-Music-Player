/*
 * Describes essential information for songs. Used for display purposes,
 * and for looking up songs from the music library.
 */

import java.time.Duration; 
public class Song implements Comparable<Song> {

	private String title;
	private String artist;
	private String album;
	private Duration duration; 
	
	public Song() {
		this.title = null;
		this.artist = null;
		this.album = null;
		this.duration = null;
	}
	
	public Song(String t, String a, String al, long d) {
		this.title = t;
		this.artist = a;
		this.album = al;
		this.duration = Duration.ofSeconds(d);
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
	
	public Duration getDuration() {
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
	
	public String toString() {
		return "" + title + " | " + artist + " | " + album + " | " + duration.toString();
	}
	
	
}
