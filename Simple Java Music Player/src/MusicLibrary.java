/*
 * Stores a map of song titles to filepaths, used for playing songs
 * 
 * Expected hierarchy: 
 * 					Main Folder -> 
 * 								Artist1 ->
 *                                    Album1 ->
 *                                         Song1
 *                                         Song2
 *                                         ...
 *                                    ...
 *                              ...
 *                     ....
 */

import java.util.HashMap;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.TagException;
import javafx.collections.ObservableList;

public class MusicLibrary {
	
	private HashMap<String, Path> library;
	private Path mainFolder; 
	private Playlist globalPlaylist;
	private int numSongs;
	boolean loaded;
	
	public MusicLibrary() {
		library = new HashMap<String,Path>();
		mainFolder = null;
		numSongs = 0;
	}
	
	public MusicLibrary(Path folderPath) {
		library = new HashMap<String,Path>();
		mainFolder = folderPath;
		globalPlaylist = new Playlist("Library");
		numSongs = 0;
	}
	
	public void setMainFolder(Path folderPath) {
		mainFolder = folderPath;
	}
	
	public ObservableList<Unit> getUnits(){
		return globalPlaylist.getPlaylist();
	}
	
	public Playlist getPlaylist(){
		return globalPlaylist;
	}
	
	public Path getSongPath(String title){
		return library.get(title);
	}
	
	public int getNumSongs() {
		return numSongs;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	
	public void loadMusicLibrary() {
		
		if(mainFolder == null){
			System.out.println("Main Music Folder not set!");
			return;
		}
		
		
		
		try(DirectoryStream<Path> artistStream = Files.newDirectoryStream(mainFolder)) {	
			
			
			for (Path artistPath: artistStream) { 		
				if(Files.isDirectory(artistPath)) {				
					try (DirectoryStream<Path> albumStream = Files.newDirectoryStream(artistPath)){ 
						for(Path albumPath : albumStream) {
							if(Files.isDirectory(albumPath)) {
								try(DirectoryStream<Path> songStream = Files.newDirectoryStream(albumPath)) {
								
									for(Path songPath: songStream) {
										
										String fileName = songPath.getFileName().toString();
										int fileNameLength = fileName.length();
										String extension = fileName.substring(fileNameLength - 3);
										
										if(extension.equals("m4a")|| extension.equals("mp3") || extension.equals("mp4") || extension.equals("aac")) {
											File songFile = songPath.toFile();
											
											try{
									
											AudioFileIO inst = new AudioFileIO();
											AudioFile f = inst.readFile(songFile);
											Tag tag = f.getTag();
											AudioHeader header = f.getAudioHeader();
											
										
											
											String title = tag.getFirst(FieldKey.TITLE);
											String artist = tag.getFirst(FieldKey.ARTIST);
											String album = tag.getFirst(FieldKey.ALBUM);
											long duration = (long)header.getTrackLength();	
										
											
											if(title == ""){
												title = fileName.substring(0,fileNameLength - 4);
											}
											if(artist == "") {
												artist = artistPath.getFileName().toString();
											}
											
											if(album == "") {
												album = albumPath.getFileName().toString();
											}
											
											Song currentSong = new Song(title,artist,album,duration);
											globalPlaylist.addSingleSong(currentSong);
											library.put(title, songPath);
											numSongs++;
											}
											catch(TagException e) {
												System.out.println("Tag Exception occured.");
											} 
											catch(CannotReadException e){
												System.out.println("Song File could not be read.");
											} 
											catch(ReadOnlyFileException e) {
												System.out.println("Song is read only");
											} catch(InvalidAudioFrameException e) {
												e.printStackTrace();
											}	
										}					
									}	
									songStream.close();
								}
								}	
						}		
						albumStream.close();
					}
				}
				
				}
			
			artistStream.close();
		loaded = true;			
		}catch (DirectoryIteratorException e) {
			e.printStackTrace();
		} catch (IOException e ) {
			e.printStackTrace();
		}

	}
}
