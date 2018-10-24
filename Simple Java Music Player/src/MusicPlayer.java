/*
 * Loads and stores music library, plays, pauses, skips
 * 
 * 
 * 
 */

import javafx.application.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.collections.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.Media;

import javafx.scene.control.ContextMenu;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Iterator;
import java.util.Deque;
import java.util.Stack;
import java.util.LinkedList;

import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import java.util.HashMap;

import  javafx.scene.input.MouseEvent;
import java.util.logging.LogManager;
import javafx.concurrent.Task;



public class MusicPlayer extends Application {
	
	private MusicLibrary musicLibrary;
	private ObservableList<Playlist> playlists;
	
	private Stage window;
	
	private BorderPane borderPane;
	private Scene mainScene;
	
	private VBox songView;
	private TableView<Unit> songTable;
	private TableView<Unit> playlistTable;
	
	boolean libraryLoaded;
	boolean shuffle;
	boolean repeat;
	private ObservableList<Unit> nowPlaying;
	private Iterator<Unit> musicIterator;
	private Unit currentUnit;
	private Song currentSong;
	private Deque<Song> songQueue;
	private Stack<Song> songHistory;
	
	private ContextMenu songOption;
	
	private MediaPlayer mediaPlayer;
	private Button skipRightButton;
	private Button playButton;
	private Button skipLeftButton;
	private Button shuffleButton;
	private Button repeatButton;
	
	private Slider progressSlider;
	private Slider volumeSlider;
	private Label currentTime;
	private Label totalDuration;
	private Label titleLabel;
	
	private String newPlaylistTitle;

	
	private HashMap<String, Playlist> unsavedPlaylists;
	
	
	private void setLibraryDirectory() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File mainDirectory = dirChooser.showDialog(window);
		
		if(mainDirectory != null) {
			Path libraryDirectory = Paths.get(mainDirectory.getAbsolutePath());
			musicLibrary = new MusicLibrary(libraryDirectory);
			
			loadChosenLibrary();	
		}	
	}
	
	
	// Changes what is displayed in the center of the border[pane
	private void switchMainView(Node v) {
		songView.getChildren().clear();
		songView.getChildren().add(v);
	}
	
	private void setNowPlaying(ObservableList<Unit> o) {
		nowPlaying = o;
	}
	
	public void enableMusicControls() {
		playButton.disableProperty().set(false);
		skipLeftButton.disableProperty().set(false);
		skipRightButton.disableProperty().set(false);
	}
	
	@Override
	public void start(Stage firstStage) throws Exception {
		
		window = firstStage;
		window.setTitle("Simple Java Music Player");
		
		songQueue = new LinkedList<Song>();
		songHistory = new Stack<Song>();
		
		shuffle = false;
		repeat = false;
		
		musicIterator = null;
		currentUnit = null;

		playlists = FXCollections.observableArrayList();
		loadSavedPlaylists();
		unsavedPlaylists = new HashMap<String, Playlist>();
		
		libraryLoaded = false;
		
		borderPane = new BorderPane();
		
		songView = new VBox();
		
		Button browse = createBrowseButton("No songs loaded. Select a music directory...");
		
		songTable = new MusicLibraryTable();
		MusicLibraryTable sT = (MusicLibraryTable)songTable;
		sT.setPlaceholderButton(browse);
		
		
		
		songView.getChildren().addAll(songTable);
				
		//Menu
		Menu fileMenu = new Menu("File");
		
		//Menu Items
		MenuItem dirChange = new MenuItem("Change Music Directory...");
		dirChange.setOnAction(browse.getOnAction());
		
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(e-> closeProgram());
		fileMenu.getItems().addAll(dirChange, exit);
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu);
		
		
		VBox leftMenu = new VBox();
		
		leftMenu.setMinHeight(500);
		leftMenu.setPrefHeight(700);
		leftMenu.setMaxHeight(900);
		
		
		ListView<Label> libraryMenu = new ListView<Label>();
		Label libLabel = new Label("Music Library");
		
		
		libraryMenu.getItems().add(libLabel);
		
		libraryMenu.setOnMouseClicked(c -> {
	        if (c.getButton()==MouseButton.PRIMARY ) {
	        	switchMainView(songTable);
		    }
	    });
		
		
		
		ListView<Playlist> playlistMenu = new PlaylistMenu(playlists);
		
		playlistMenu.setOnMouseClicked(c -> {
	        if (c.getButton()==MouseButton.PRIMARY ) {
		            Playlist selected = playlistMenu.getSelectionModel().getSelectedItem();
		            playlistTable = new PlaylistTable(selected);
		            
		            playlistTable.setRowFactory(tv -> {
					    TableRow<Unit> row = new TableRow<>();
					    
					    row.setOnMouseClicked(cl -> {
					        if (! row.isEmpty() && cl.getButton()==MouseButton.PRIMARY 
					             && cl.getClickCount() == 2) {
					        	
					        	if(libraryLoaded) {
					        	      Unit selectedUnit = row.getItem();
							           PlaylistTable musicTable = (PlaylistTable)playlistTable;
							           resetQueue(selectedUnit, musicTable.getPlaylist());
							           Platform.runLater(() -> playNextSong());
							          
					        	}
					        } else if(! row.isEmpty() && c.getButton()==MouseButton.SECONDARY) {
					        	if(songOption!=null && songOption.isShowing())
					        		songOption.hide();
					        	songOption = createSongOptionMenu(playlistTable);
					        	songOption.show(playlistTable, c.getScreenX(), c.getScreenY());
					        } 
					    });
					    return row;
		            });
					    
		            
		            
		            
		            playlistTable.getPlaceholder().setOnMouseClicked(e -> {
		            	switchMainView(songTable);
		            });
		            

		            switchMainView(playlistTable);
		        }
		    });
		
		
		Button newPlaylistButton = new Button("(+) New Playlist");
	
		
		
		
		
		newPlaylistButton.setMaxWidth(leftMenu.maxWidthProperty().doubleValue());
		
		
		TitledPane libraryPane = new TitledPane("Your Music", libraryMenu);
		libraryPane.setCollapsible(false);
		TitledPane playlistPane = new TitledPane("Playlists", playlistMenu);
		playlistPane.setCollapsible(false);
		
		newPlaylistButton.setOnMouseClicked(e -> {
			newPlaylistTitle = NewPlaylistPrompt.display();
			if(newPlaylistTitle != null) {
				Playlist pl = new Playlist(newPlaylistTitle);
				unsavedPlaylists.put(newPlaylistTitle, pl);
				playlists.add(pl);
			}
		});
		
		
		leftMenu.getChildren().addAll(libraryPane,playlistPane, newPlaylistButton);
		
		
		VBox botMenu = new VBox();
		HBox playerButtons = new HBox();
		playButton= new Button(">");
		skipLeftButton= new Button("<<");
		skipRightButton= new Button(">>");
		shuffleButton= new Button("Shuffle");
		repeatButton= new Button("Repeat");
		
		
		playButton.setMinSize(100, 100);
		skipLeftButton.setMinSize(50, 50);
		skipRightButton.setMinSize(50, 50);
		shuffleButton.setMinSize(25, 25);
		repeatButton.setMinSize(25, 25);
		
		
		playButton.disableProperty().set(true);
		skipLeftButton.disableProperty().set(true);
		skipRightButton.disableProperty().set(true);
		
		playButton.setOnMouseClicked( c -> {
			if(mediaPlayer == null) {
		         Platform.runLater(() -> playNextSong());
			} else if (mediaPlayer.getStatus() == Status.PLAYING) {
				mediaPlayer.pause();
			} else if (mediaPlayer.getStatus() == Status.PAUSED) {
				mediaPlayer.play();
			}
		});
		
		skipRightButton.setOnMouseClicked(c -> {
			playNextSong();
		});
		
		skipLeftButton.setOnMouseClicked(c -> {
			playPreviousSong();		
		});
		
		shuffleButton.setOnMouseClicked(c -> {
			if(!shuffle)
				shuffleButton.setStyle("-fx-text-fill: green");
			else
				shuffleButton.setStyle("-fx-text-fill: black");
			shuffle = !shuffle;
		});
		
		repeatButton.setOnMouseClicked(c -> {
			if(!repeat)
				repeatButton.setStyle("-fx-text-fill: green");
			else
				repeatButton.setStyle("-fx-text-fill: black");
			repeat = !repeat;
		});
		
		
		HBox titleBox = new HBox();
		titleLabel = new Label();
		titleBox.setAlignment(Pos.CENTER);
		titleBox.getChildren().add(titleLabel);
		
		HBox progressBar = new HBox();
		
		progressSlider = new Slider();
		progressSlider.setMin(0);
		progressSlider.setMax(1000);
		
		
		
		progressSlider.disableProperty().set(true);
		progressSlider.setMinWidth(300);
		progressSlider.setPrefWidth(400);
		progressSlider.setMaxWidth(500);
		
		
		
		progressSlider.setOnMouseReleased((new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				progressSlider.setValueChanging(true);
	            double value = (e.getX()/progressSlider.getWidth())*progressSlider.getMax();
	            progressSlider.setValue(value);
	            progressSlider.setValueChanging(false);
			}
		}));
		
		
		progressSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				if(progressSlider.isValueChanging()) {
					if(mediaPlayer != null) {
						Media curr = mediaPlayer.getMedia();
						mediaPlayer.seek(curr.getDuration().multiply(progressSlider.getValue() / 1000.0));
					}
				}
			}
		});
		
		progressSlider.setMinWidth(50);
		progressSlider.setMaxWidth(Double.MAX_VALUE);
		
		Label vol = new Label("Volume");
		
		volumeSlider = new Slider();
		volumeSlider.setMaxWidth(70);
		volumeSlider.setMinWidth(30);
		volumeSlider.setValue(25);
		
		volumeSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				volumeSlider.setValueChanging(true);
	            double value = (e.getX()/volumeSlider.getWidth())*volumeSlider.getMax();
	            volumeSlider.setValue(value);
	            volumeSlider.setValueChanging(false);
			}
		});
		
		
		volumeSlider.valueProperty().addListener( new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				if (volumeSlider.isValueChanging()) {
					if(mediaPlayer != null) {
					mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);;
					}
				}
			}	
		});
		
		
		currentTime = new Label("-:--");
		totalDuration = new Label("-:--");
		
		progressBar.setAlignment(Pos.CENTER);
		progressBar.setSpacing(20);
		
		progressBar.getChildren().addAll(currentTime, progressSlider, totalDuration, vol, volumeSlider);

		playerButtons.setSpacing(20);
		playerButtons.getChildren().addAll(shuffleButton, skipLeftButton, playButton, skipRightButton, repeatButton);
		playerButtons.setAlignment(Pos.CENTER);
		
		botMenu.getChildren().addAll(titleBox, progressBar, playerButtons);
		
		botMenu.setAlignment(Pos.BOTTOM_RIGHT);
				
		borderPane.setTop(menuBar);
		borderPane.setLeft(leftMenu);
		borderPane.setCenter(songView);
		borderPane.setBottom(botMenu);
		
		
		
		mainScene = new Scene(borderPane, 1200, 800);
		
		window.setScene(mainScene);
		
		window.setOnCloseRequest(e ->{
			e.consume();
			closeProgram();
		});
		
		window.show();
	}
	
	private ContextMenu createSongOptionMenu(TableView<Unit> source) {
		
		ContextMenu optionMenu = new ContextMenu();
				
		ObservableList<Unit> selected = source.getSelectionModel().getSelectedItems();
		Menu subMen = new Menu("Add to playlist");
		MenuItem addtoQueue = new MenuItem("Add to Queue");
		
		for(Playlist p : playlists) {
			MenuItem playlistItem = new MenuItem(p.getTitle());
			
			playlistItem.setOnAction( e-> {
				for(Unit u : selected) {
	        		p.addUnit(u);
	        	}
	        	unsavedPlaylists.put(p.getTitle(), p);
		    });
			
			subMen.getItems().add(playlistItem);
		}	
		
		optionMenu.getItems().add(subMen);
		
		if(selected.size() > 1) {
			Menu addAsUnit = new Menu("Add to Playlist as Unit");
			
			for(Playlist p : playlists) {
				MenuItem playlistItem = new MenuItem(p.getTitle());
				
				playlistItem.setOnAction( e-> {
					Unit u = Playlist.combineUnits(selected);
					p.addUnit(u);
		        	unsavedPlaylists.put(p.getTitle(), p);
			    });
				
				addAsUnit.getItems().add(playlistItem);
			}
			
			optionMenu.getItems().add(addAsUnit);
			
			addtoQueue.setOnAction(e-> {
				for(Unit u: selected) {
				
					if (u.getUnitType() == UnitType.MULTI) {
						
						MultiSongUnit m = (MultiSongUnit) u;
						songQueue.addAll(m.getSongs());
					} else {
						SingleSongUnit s = (SingleSongUnit) u;
						songQueue.add(s.getSong());
					}
					
				}
				
			});
			
		} else {
			addtoQueue.setOnAction(e-> {
				SingleSongUnit u = (SingleSongUnit) selected.get(0);
				songQueue.add(u.getSong());
			});
		}
		
		optionMenu.getItems().add(addtoQueue);
		
		optionMenu.setAutoHide(true);
		
		
		//MenuItem removeFromPlaylist = new MenuItem("Remove from this Playlist");
		
		return optionMenu;
	}
		

	private Button createBrowseButton(String message) {
		Button browse = new Button();
		browse.setText(message);
		
		browse.setOnAction(e -> {	
			
			setLibraryDirectory();
				
		});
		
		return browse;
	}
	
	
	protected void loadChosenLibrary() {
			
		 Task<Void> task = new Task<Void>() { // load music library in background
			 public Void call() throws InterruptedException {
				 
				 musicLibrary.loadMusicLibrary();
				 
			     return null;
			 }
		};
		
		
		task.setOnSucceeded(e -> {
			
			 if(musicLibrary.getNumSongs() == 0) {
					MusicLibraryTable sT = (MusicLibraryTable)songTable;
					sT.getPlaceholderButton().setText("Selected directory is either invalid or empty. Expected folder hierarchy is: Artist -> Album -> Song");
					libraryLoaded = false;
					switchMainView(songTable);
				} else {
					songTable = new MusicLibraryTable(musicLibrary.getPlaylist());
					
					libraryLoaded = true;
					enableMusicControls();
					nowPlaying = null;
					songTable.setRowFactory(tv -> {
					    TableRow<Unit> row = new TableRow<>();
					    
					    row.setOnMouseClicked(c -> {
					        if (! row.isEmpty() && c.getButton()==MouseButton.PRIMARY 
					             && c.getClickCount() == 2) {
					            Unit selected = row.getItem();
					            MusicLibraryTable musicTable = (MusicLibraryTable)songTable;
					            resetQueue(selected, musicTable.getPlaylist());
					            Platform.runLater(() -> playNextSong());
					          
					        } else if(! row.isEmpty() && c.getButton()==MouseButton.SECONDARY) {
					        	if(songOption!=null && songOption.isShowing())
					        		songOption.hide();
					        	songOption = createSongOptionMenu(songTable);
					        	songOption.show(songTable, c.getScreenX(), c.getScreenY());
					        } 
					    });
					    
					    return row ;
					    
					});
					
					switchMainView(songTable);
				}
			
		});
		
		task.setOnFailed(e -> task.getException().printStackTrace());
		
		new Thread(task).start();
		
	}
	
	
	
	
	protected void updateMediaDisplay() {
		
		Platform.runLater(new Runnable(){
			
			public void run() {
				Duration curr=  mediaPlayer.getCurrentTime();
				Duration total = mediaPlayer.getMedia().getDuration();
				
				double currSeconds = curr.toMillis();
				double totalSeconds = total.toMillis();
				double percentage = (currSeconds/totalSeconds);
				
				if(!progressSlider.isValueChanging()) {
					progressSlider.valueProperty().setValue((percentage * 1000.0));
				}	
				
				if (progressSlider.disableProperty().getValue()){
					progressSlider.disableProperty().set(false);
				}
				
				
				if(mediaPlayer.getStatus() == Status.PAUSED || mediaPlayer.getStatus() == Status.STOPPED ) {
					playButton.setText(">");
				} else if(mediaPlayer.getStatus() == Status.PLAYING) {
					playButton.setText("||");
				}
				currentTime.setText(Song.formatDuration(curr));
				totalDuration.setText(Song.formatDuration(total));
				titleLabel.setText(currentSong.getTitle() + " - " + currentSong.getArtist());		
				
				if(songHistory.isEmpty()) {
					skipLeftButton.disableProperty().set(true);
				}
			}
			
		}); 
	}
		
	
	
	
	private void resetQueue(Unit u, Playlist currentPlaylist) {
		if(mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
			mediaPlayer.stop();
		}
		
		
		ObservableList<Unit> sortOrder;
		
		if(shuffle) {
			sortOrder = currentPlaylist.getShuffledCopy(u);
		}else {
			sortOrder = currentPlaylist.getPlaylistCopy();
		}
			
		setNowPlaying(sortOrder);	
		currentUnit = u;
		currentSong = null;
		musicIterator = null;
		songQueue.clear();
		songHistory.clear();
		skipLeftButton.setDisable(true);
	}
	
		
	private void playNextSong() {
		if (songQueue.isEmpty()) {
			if(currentUnit!=null)
				queueUnit(currentUnit);
			
			if(!songQueue.isEmpty()) {
				playNextSong();
			}else {			
				//System.out.println("End of playable songs");
				if(mediaPlayer!=null) {
					mediaPlayer.stop();
					updateMediaDisplay();
				}					
			}
		} else {
			Song s = songQueue.poll();
			URI songURI = musicLibrary.getSongPath(s.getTitle()).toUri();
			Media m = new Media(songURI.toString());
			
			if(mediaPlayer != null) {
				mediaPlayer.dispose();
				songHistory.push(currentSong);
			}
			
			mediaPlayer = new MediaPlayer(m);
			
			mediaPlayer.setVolume(volumeSlider.getValue());
			

			mediaPlayer.setOnEndOfMedia(() ->{
				playNextSong();
			});
			
			mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime)-> {
				 updateMediaDisplay();
			});		
			
			mediaPlayer.statusProperty().addListener((obs, oldStat, newStat)-> {
				 updateMediaDisplay();
			});
			currentSong = s;
			mediaPlayer.play();
		}
	}
	
	private void playPreviousSong() {
		if (!songHistory.isEmpty()) {
			Song s = songHistory.pop();
			URI songURI = musicLibrary.getSongPath(s.getTitle()).toUri();
			Media m = new Media(songURI.toString());
			
			if(mediaPlayer != null) {
				mediaPlayer.dispose();
				songQueue.addFirst(currentSong);
			}
			
			mediaPlayer = new MediaPlayer(m);
			
			mediaPlayer.setVolume(volumeSlider.getValue());
			

			mediaPlayer.setOnEndOfMedia(() ->{
				playNextSong();
			});
			
			mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime)-> {
				 updateMediaDisplay();
			});		
			
			mediaPlayer.statusProperty().addListener((obs, oldStat, newStat)-> {
				 updateMediaDisplay();
			});
			
			currentSong = s;
			mediaPlayer.play();
		}
	}
	
		
	
// sets current unit and refills queue
	private void queueUnit(Unit u) {	
		
		if(currentUnit == null) { //No song clicked, pick first unit in list
			if(nowPlaying == null) {
				return;
			} else {
				currentUnit = nowPlaying.get(0);
			}
		}else if(musicIterator == null) { // Song clicked, find appropriate unit and use as start position
			musicIterator = nowPlaying.iterator();
			boolean found = false;
			Unit curr = null;
			while(musicIterator.hasNext() && !found) {
				curr = musicIterator.next();
				if(curr.equals(u)){
					found = true;	
				}	
			}
			if(found) {
				currentUnit = curr;
			} else {
				AlertBox.display("Alert", "Selected songs(s) missing from the current playlist");
				return;
			}
		} else { // In the process of playing, or just ended
			if(musicIterator!=null && musicIterator.hasNext())
				currentUnit = musicIterator.next();
			else if(musicIterator!=null && repeat) {
				musicIterator = nowPlaying.iterator();
				currentUnit = musicIterator.next();
			} else {
				currentUnit = null;
				return;
			}
		}
		
		if (currentUnit.getUnitType() == UnitType.SINGLE)  {
			SingleSongUnit c = (SingleSongUnit)currentUnit;
			songQueue.add(c.getSong());
		} else {
			MultiSongUnit c = (MultiSongUnit)currentUnit;
			songQueue.addAll(c.getSongs());
		}
		
	}
	
	private void loadSavedPlaylists(){
		Path playlistPath = Paths.get("Playlists");
		
		try{
		DirectoryStream<Path> stream =  Files.newDirectoryStream(playlistPath);
		
		for (Path plistFile: stream) {
			if(plistFile.toString().endsWith(".json")) {
				
				try{
					Playlist p = Playlist.createPlaylistFromJSON(plistFile.toFile());
					playlists.add(p);
					
				} catch (Exception pe) {
					pe.printStackTrace();
				}	
			}
		}
		
		stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void saveUnsavedPlaylists() {
		for(Playlist p: unsavedPlaylists.values()) {
			String fileName = p.getTitle().replace(" ", "");
			try {
			p.savePlaylistAsJSON(fileName);
			} catch(Exception e) {
				AlertBox.display("Error", "There was a problem saving playlist: " + p.getTitle() + ".\n " + e.toString());
			}
		}
	}
	
	
	private void closeProgram() {

		saveUnsavedPlaylists();
		window.close();	
	}
	
	public static void main(String[] args){
		LogManager.getLogManager().reset();
		launch(args);
	}
		
	
	

}
