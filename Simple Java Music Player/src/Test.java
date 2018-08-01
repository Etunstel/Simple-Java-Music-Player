import java.io.FileNotFoundException;
import java.time.Duration;
public class Test {

	
	final static String PLAYLISTDIRECTORY = "src\\Playlists\\";
	
	public static void main(String[] args) {
		
		Playlist test = new Playlist();
		
		try{
		test = Playlist.createPlaylistFromJSON(PLAYLISTDIRECTORY + "testPlaylist.json");
		System.out.println(test.toString());
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
		test.savePlaylistAsJSON(PLAYLISTDIRECTORY + "scoopityDoop.json");
		System.out.println("Done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

	}

}
