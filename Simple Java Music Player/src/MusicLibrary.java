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
public class MusicLibrary {
	
	private HashMap<String, Path> library;
	private Path mainFolder; 
	private ArrayList<Song> songs;
	
	public MusicLibrary() {
		library = new HashMap<String,Path>();
		mainFolder = null;
	}
	
	public MusicLibrary(Path folderPath) {
		library = new HashMap<String,Path>();
		mainFolder = folderPath;
	}
	
	public void setMainFolder(Path folderPath) {
		mainFolder = folderPath;
	}

	
	
	public void loadMusicLibrary() throws IOException{
		//for each entry in mainfolder:
		//if entry is directory, traverse into Artist folder, note name
		// for each album in current dir: traverse into
		// If file has a music extension (aac/mp3/mp4/m4a/etc):
		// get name and duration, create song and store in SongList, store the song's path in Map indexed to name
		// If no Title attribute, use the filename, but otherwise use the Title
		// Duration is Length attribute under "Media"
		
		try(DirectoryStream<Path> artistStream = Files.newDirectoryStream(mainFolder)) {
			
			for (Path artist: artistStream) { //artist folder
				
				if(Files.isDirectory(artist)) {
					System.out.println("Artist: " + artist.getFileName() + "\n");
					
					try (DirectoryStream<Path> albumStream = Files.newDirectoryStream(artist)){ //album folder
						
						for(Path album : albumStream) {
							if(Files.isDirectory(album)) {
								System.out.println("Album: " + album.getFileName() + "\n");
								
								try(DirectoryStream<Path> songStream = Files.newDirectoryStream(album)) {
								
									for(Path song: songStream) {
										
										System.out.println("Song: " + song.getFileName() + "\n");
										BasicFileAttributes attrs = Files.readAttributes(song, BasicFileAttributes.class);
										
										System.out.println("Attrs: " + attrs.toString()); //wrong
									}	
								}
							}	
							}
								
					}
					
					System.out.println("--------------------------------------------");
				}
				
				}
					
		}catch (DirectoryIteratorException e) {
			System.out.println("Error Loading songs from the specified folder. Expected heirarchy is Artists -> Albums -> Songs.");
			throw e.getCause();
		}
		
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
