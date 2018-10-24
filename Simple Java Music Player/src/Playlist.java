/* 
 *  Represents a playlist as a list of Units
 *  Has a static methods for creating Playlist objects from saved JSON files, 
 *  and a non-static method for exporting the current Playlist to a new JSON file.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ListIterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.util.LinkedList;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;

public class Playlist {
	
	private String title; 	
	private ObservableList<Unit> playlist;
	
	public Playlist() {
		title = "";
		playlist = FXCollections.observableArrayList(new LinkedList<Unit>());
	}
	
	public Playlist(String title) {
		this.title = title;
		playlist = FXCollections.observableArrayList(new LinkedList<Unit>());
	}
	
	public boolean addSingleSong(Song s) {
		return playlist.add(new SingleSongUnit(s));
	}
	
	public boolean addUnit(Unit u) {
		return playlist.add(u);
	}
	
	public boolean removeUnit(Unit u) {
		return playlist.remove(u);
	}


	public void setTitle(String t) {
		this.title = t;
	}
	
	public String getTitle() {
		return title;
	}
	
	public SimpleStringProperty getTitleAsStringProperty() {
		return new SimpleStringProperty(title);
	}
	
	public ObservableList<Unit> getPlaylist() {
		return playlist;
	}
	
	public ObservableList<Unit> getPlaylistCopy() {
		return FXCollections.observableArrayList(playlist);
	}
	
	public ObservableList<Unit> getShuffledCopy() {
		ObservableList<Unit> copy = FXCollections.observableArrayList(playlist);
		FXCollections.shuffle(copy);
		return copy;
	}
	
	//Shuffles and adds u as the first unit
	public ObservableList<Unit> getShuffledCopy(Unit u) {
		ObservableList<Unit> copy = FXCollections.observableArrayList(playlist);
		copy.remove(u);
		FXCollections.shuffle(copy);	
		copy.add(0, u);
		return copy;
	}
	
	
	//throw multiple?
	public static Playlist createPlaylistFromJSON(File f) throws FileNotFoundException, ParseException, IOException{
		
		Playlist p = new Playlist();
		
		Object obj = new JSONParser().parse(new FileReader(f));
		
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
				Double duration = (double)song.get("duration");
				
				Song s = new Song(title, artist, album, duration.doubleValue());
				
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
					Double duration = (double)song.get("duration");
					
					Song s = new Song(title, artist, album, duration.doubleValue());
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
				currentSong.put("duration", s.getDuration().toSeconds());
					
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
					currentSong.put("duration", s.getDuration().toSeconds());
					
					unitSongs.add(currentSong);
				}
				
				currentUnit.put("type", "multi");
				currentUnit.put("songs" , unitSongs);
				
				units.add(currentUnit);
			}
		}
		
		root.put("title", this.title);
		root.put("units", units);
		
		Path relative = Paths.get("");
		String s = relative.toAbsolutePath().toString() + "\\Playlists" + "\\" + fileName + ".json";
		System.out.println(s);
		File playlistFile = new File(s);
			
		try{
		playlistFile.getParentFile().mkdirs();
		if(!playlistFile.exists())
			playlistFile.createNewFile();
		} catch(IOException e) {
			System.out.println("Failed to create new file");
			e.printStackTrace();
			return;
		}
		
		PrintWriter pw = new PrintWriter(playlistFile);
		pw.print(root.toJSONString().toString());
		pw.flush();
		pw.close();
	}
	
	public static Unit combineUnits(ObservableList<Unit> units) {
		if (units.size() == 1) {
			return units.get(0);
		} 
		
		ArrayList<Song> songs = new ArrayList<Song>();
		
		for(Unit u: units) {
			if (u.getUnitType() == UnitType.SINGLE) {
				SingleSongUnit s = (SingleSongUnit) u;
				songs.add(s.getSong());
			} else {
				MultiSongUnit s = (MultiSongUnit) u;
				songs.addAll(s.getSongs());
			}
		}
		return new MultiSongUnit(songs);
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
	
	
}
