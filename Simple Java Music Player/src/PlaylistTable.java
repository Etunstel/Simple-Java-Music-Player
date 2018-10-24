/*
 * Displays an individual Playlist made of single/multisong Units
 * 
 */


import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn.CellDataFeatures;

import javafx.util.Duration;
import java.util.ArrayList;
public class PlaylistTable extends TableView<Unit> {	

	private Playlist playlist;
	
	public ObservableList<Unit> getCurrentSort() {
		return playlist.getPlaylistCopy();
	}
	
	public Playlist getPlaylist() {
		return playlist;
	}
	
	public void setPlaceholderButton(Button b) {
		setPlaceholder(b);
	}
	
	public PlaylistTable(Playlist p) {
		
		playlist = p;
		
		//Title column
		TableColumn<Unit,String> titleColumn = new TableColumn<>("Title");
		titleColumn.setMinWidth(100);
		titleColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	Unit u = c.getValue();
		    	if(u.getUnitType() == UnitType.SINGLE) {
		        	SingleSongUnit s = (SingleSongUnit) u;
			        return new SimpleStringProperty(s.getSong().getTitle());
		    	} else {
		    		MultiSongUnit m = (MultiSongUnit) u;
		    		ArrayList<Song> songs = m.getSongs();
		    		String titles = "";
		    		for(Song s: songs) {
		    			if(titles.equals(""))
		    				titles = titles + s.getTitle();
		    			else
		    				titles = titles + "\n" + s.getTitle();
		    		}
		    		return new SimpleStringProperty(titles);
		    	}	
		    }
		});
		
		//Artist column
		TableColumn<Unit,String> artistColumn = new TableColumn<>("Artist");
		artistColumn.setMinWidth(100);
		artistColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	Unit u = c.getValue();
		    	if(u.getUnitType() == UnitType.SINGLE) {
		        	SingleSongUnit s = (SingleSongUnit) u;
			        return new SimpleStringProperty(s.getSong().getArtist());
		    	} else {
		    		MultiSongUnit m = (MultiSongUnit) u;
		    		ArrayList<Song> songs = m.getSongs();
		    		String artists = "";
		    		for(Song s: songs) {
		    			if(artists.equals(""))
		    				artists = artists + s.getArtist();
		    			else
		    				artists = artists + "\n" + s.getArtist();
		    		}
		    		return new SimpleStringProperty(artists);
		    	}	
		    }
		});
				
		//Album column
		TableColumn<Unit,String> albumColumn = new TableColumn<>("Album");
		albumColumn.setMinWidth(100);
		albumColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	Unit u = c.getValue();
		    	if(u.getUnitType() == UnitType.SINGLE) {
		        	SingleSongUnit s = (SingleSongUnit) u;
			        return new SimpleStringProperty(s.getSong().getTitle());
		    	} else {
		    		MultiSongUnit m = (MultiSongUnit) u;
		    		ArrayList<Song> songs = m.getSongs();
		    		String albums = "";
		    		for(Song s: songs) {		
		    			if(albums.equals(""))
		    				albums = albums + s.getAlbum();
		    			else
		    				albums = albums + "\n" + s.getAlbum();
		    		}
		    		return new SimpleStringProperty(albums);
		    	}	
		    }
		});
		
		//Length column
		TableColumn<Unit,String> durationColumn = new TableColumn<>("Length");
		durationColumn.setMinWidth(50);
		durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

		// Custom rendering of the table cell.
		
		durationColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		        Unit u = c.getValue();
		    	if(u.getUnitType() == UnitType.SINGLE) {
		    		SingleSongUnit s = (SingleSongUnit) c.getValue();
			    	Duration d = s.getSong().getDuration();
			        return new SimpleStringProperty(Song.formatDuration(d));
		    	} else {
		    		MultiSongUnit m = (MultiSongUnit) u;
		    		ArrayList<Song> songs = m.getSongs();
		    		String durations = "";
		    		for(Song s: songs) {
		    			if(durations.equals(""))
		    				durations = durations + Song.formatDuration(s.getDuration());
		    			else
		    				durations = durations + "\n" + Song.formatDuration(s.getDuration());
		    		}
		    		return new SimpleStringProperty(durations);
		    	}	
		    }
		});
		
		

		getColumns().addListener(new ListChangeListener<Object>() { 
			public boolean suspended; 
			@Override 
			public void onChanged(Change change) { 
				change.next(); 
				if (change.wasReplaced() && !suspended) { 
					this.suspended = true; 
					getColumns().setAll(titleColumn, artistColumn, albumColumn, durationColumn); 
					this.suspended = false; 
					} 
				} 
		});
		
		setMinHeight(500);
		setPrefHeight(700);
		setMaxHeight(900);
		
		setItems(playlist.getPlaylist());
		getColumns().addAll(titleColumn, artistColumn,albumColumn, durationColumn);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		Button leave = new Button();
        leave.setText("Return to the library to add music to this playlist");
        setPlaceholder(leave);
	}	
	
	
}