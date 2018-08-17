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
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.TagException;

public class MusicLibrary {
	
	private HashMap<String, Path> library;
	private Path mainFolder; 
	private ArrayList<Song> songList;
	
	public MusicLibrary() {
		library = new HashMap<String,Path>();
		mainFolder = null;
		songList =  new ArrayList<Song>();
	}
	
	public MusicLibrary(Path folderPath) {
		library = new HashMap<String,Path>();
		mainFolder = folderPath;
		songList =  new ArrayList<Song>();
	}
	
	public void setMainFolder(Path folderPath) {
		mainFolder = folderPath;
	}
	
	public ArrayList<Song> getSongs(){
		return songList;
	}
	
	public Path getSongPath(String title){
		return library.get(title);
	}
	
	public void loadMusicLibrary() throws IOException{
		//for each entry in mainfolder:
		//if entry is directory, traverse into Artist folder, note name
		// for each album in current dir: traverse into
		// If file has a music extension (aac/mp3/mp4/m4a/etc):
		// get name and duration, create song and store in SongList, store the song's path in Map indexed to name
		// If no Title attribute, use the filename, but otherwise use the Title
		// Duration is Length attribute under "Media"
		
		if(mainFolder == null){
			System.out.println("Main Music Folder not set!");
			return;
		}
		
		try(DirectoryStream<Path> artistStream = Files.newDirectoryStream(mainFolder)) {
			
			for (Path artistPath: artistStream) { //artist folder
				
				if(Files.isDirectory(artistPath)) {
					//System.out.println("Artist: " + artistPath.getFileName() + "\n");
					
					try (DirectoryStream<Path> albumStream = Files.newDirectoryStream(artistPath)){ //album folder
						
						for(Path albumPath : albumStream) {
							if(Files.isDirectory(albumPath)) {
								//System.out.println("Album: " + albumPath.getFileName() + "\n");
								
								try(DirectoryStream<Path> songStream = Files.newDirectoryStream(albumPath)) {
								
									for(Path songPath: songStream) {
										
										String fileName = songPath.getFileName().toString();
										int fileNameLength = fileName.length();
										String extension = fileName.substring(fileNameLength - 3);
										
										if(extension.equals("m4a")|| extension.equals("mp3") || extension.equals("mp4") || extension.equals("aac")) {
											//System.out.println("Song: " + songPath.getFileName() + "\n");
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
											songList.add(currentSong);
											library.put(title, songPath);
											
											
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
								}
							}	
							}
								
					}
				}
				
				}
					
		}catch (DirectoryIteratorException e) {
			System.out.println("Error Loading songs from the specified folder. Expected heirarchy is Artists -> Albums -> Songs.");
			throw e.getCause();
		}
		
		//System.out.println(library);
		
		return;
	}
	
	
	public static void main(String args[]) {
		String musicPath = "--";
		Path p = Paths.get(musicPath);
		
		MusicLibrary m = new MusicLibrary(p);
		
		try {
			m.loadMusicLibrary();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
