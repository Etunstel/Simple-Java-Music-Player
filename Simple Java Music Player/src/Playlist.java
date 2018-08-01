/*
 * Constructs UnitLists from saved JSON files, can export current UnitList as a new JSON file
 * Also stores information about whether the playlist is set to shuffle/repeat
 * 
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.ListIterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.util.LinkedList;
import java.util.ArrayList;


public class Playlist {
	
	private String title; 
	private boolean shuffle;
	private boolean repeat; 
	
	
	private LinkedList<Unit> playlist;
	
	public Playlist() {
		
		shuffle = false;
		repeat = false;
		playlist = new LinkedList<Unit>();
	}
	
	public boolean addUnit(Unit u) {
		return playlist.add(u);
	}
	
	public boolean removeUnit(Unit u) {
		return playlist.remove(u);
	}
	
	public boolean getShuffle() {
		return shuffle;
	}
	
	public void toggleShuffle() {
		shuffle = !shuffle;
	}
	
	public boolean getRepeat() {
		return repeat;
	}
	
	
	public void toggleRepeat() {
		repeat = !repeat;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	public String getTitle() {
		return title;
	}
	
	//throw multiple?
	public static Playlist createPlaylistFromJSON(String fileName) throws Exception {
		
		Playlist p = new Playlist();
		
		Object obj = new JSONParser().parse(new FileReader(fileName));
		
		JSONObject root = (JSONObject) obj;
		
		p.setTitle((String) root.get("title"));
		
		JSONArray units = (JSONArray) root.get("units");
		Iterator unitIterator = units.iterator();
		
		while(unitIterator.hasNext()) {
			JSONObject currentUnit = (JSONObject) unitIterator.next();
			
			String type = (String) currentUnit.get("type");
			
			if(type.equals("single")) {
				
				
				JSONObject song = (JSONObject)currentUnit.get("song");
				
				String title = (String)song.get("title");
				String artist = (String)song.get("artist");
				String album = (String)song.get("album");
				long duration = (long)song.get("duration");
				
				Song s = new Song(title, artist, album, duration);
				
				SingleSongUnit u = new SingleSongUnit(s);
				
				p.addUnit(u);
				
			} else if (type.equals("multi")) {
				
				
				JSONArray songs = (JSONArray) currentUnit.get("songs");
				ArrayList<Song> songList = new ArrayList<Song>();
				Iterator songIterator = songs.iterator();
				
				while(songIterator.hasNext()) {
					JSONObject song = (JSONObject)songIterator.next();
					
					String title = (String)song.get("title");
					String artist = (String)song.get("artist");
					String album = (String)song.get("album");
					long duration = (long)song.get("duration");
					
					Song s = new Song(title, artist, album, duration);
					songList.add(s);
				}
				
				MultiSongUnit u = new MultiSongUnit(songList);
				p.addUnit(u);
			}	
		}
		return p;
	}
	
	public void savePlaylistAsJSON(String fileName) throws FileNotFoundException {
		
		Unit curr;
		int numSongs;
		JSONObject root = new JSONObject();
		JSONArray units = new JSONArray();
		JSONObject currentSong;
		JSONObject currentUnit;
		JSONArray unitSongs;
		ListIterator<Unit> it = playlist.listIterator();
		
		while (it.hasNext()) {
			
			curr = it.next();
			numSongs = curr.numSongs();
			
			if (numSongs == 1) {
				SingleSongUnit u = (SingleSongUnit) curr;
				Song s = u.getSong();
				
				currentUnit = new JSONObject();
				currentSong = new JSONObject();
				
				currentSong.put("title" , s.getTitle());
				currentSong.put("artist", s.getArtist());
				currentSong.put("album", s.getAlbum());
				currentSong.put("duration", s.getDuration().getSeconds());
					
				currentUnit.put("type", "single"); //single song unit
				currentUnit.put("song", currentSong);
				
				units.add(currentUnit);
						
				
			} else if(numSongs > 1){
				MultiSongUnit m = (MultiSongUnit) curr;
			
				currentUnit = new JSONObject();
				unitSongs = new JSONArray();
				
				for(Song s : m.getSongs()) {
				
					currentSong = new JSONObject();
					
					currentSong.put("title" , s.getTitle());
					currentSong.put("artist", s.getArtist());
					currentSong.put("album", s.getAlbum());
					currentSong.put("duration", s.getDuration().getSeconds());
					
					unitSongs.add(currentSong);
				}
				
				currentUnit.put("type", "multi");
				currentUnit.put("songs" , unitSongs);
				
				units.add(currentUnit);
			}
		}
		
		root.put("title", this.title);
		root.put("units", units);
		
		File playlistFile = new File(fileName);
			
		try{
		playlistFile.createNewFile();
		} catch(IOException e) {
			System.out.println("Failed to create new file");
			return;
		}
		
		PrintWriter pw = new PrintWriter(playlistFile);
		pw.print(root.toJSONString().toString());
		pw.flush();
		pw.close();
	}
	
	// WRITE UNIT toString() METHODS
	public String toString() {
		String s = "";
		
		s = s + "Playlist Title: " + this.title + "\n";
		
		
		
		ListIterator<Unit> iter = playlist.listIterator();
		
		while (iter.hasNext()) {
			
			Unit current = iter.next();
			int numSongs = current.numSongs();
			
			if(numSongs == 1) {
				SingleSongUnit single = (SingleSongUnit) current;
				s = s + single.getSong().toString() + "\n";
				
			} else if (numSongs > 1) {
				MultiSongUnit multi = (MultiSongUnit) current;
				for(Song song : multi.getSongs()) {
					s = s + song.toString() + "\n";
				}
			}
			
		}
		
		return s;
	}
	
	
	public LinkedList<Unit> constructPlaylist() {
		return playlist;
	}
	
	
}
