/* 
 * Displays the music library, which only consists of single songs.
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
public class MusicLibraryTable extends TableView<Unit> {
	
	private Playlist playlist;
	Button placeholder;
	
	public void setPlaceholderButton(Button b) {
		placeholder = b;
		setPlaceholder(b);
	}
	
	public MusicLibraryTable() {
		
		//Title column
		TableColumn<Unit,String> titleColumn = new TableColumn<>("Title");
		titleColumn.setMinWidth(100);
		titleColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getTitle());
		    }
		});
		
		//Artist column
		TableColumn<Unit,String> artistColumn = new TableColumn<>("Artist");
		artistColumn.setMinWidth(100);
		artistColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getArtist());
		    }
		});
				
		//Album column
		TableColumn<Unit,String> albumColumn = new TableColumn<>("Album");
		albumColumn.setMinWidth(100);
		albumColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getAlbum());
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
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		    	Duration d = u.getSong().getDuration();
		        return new SimpleStringProperty(Song.formatDuration(d));
		    }
		});		
		
		getColumns().addAll(titleColumn, artistColumn,albumColumn, durationColumn);
		
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
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	

	public MusicLibraryTable(Playlist plist) {
		playlist = plist;	
		
		//Title column
		TableColumn<Unit,String> titleColumn = new TableColumn<>("Title");
		titleColumn.setMinWidth(100);
		titleColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getTitle());
		    }
		});
		
		//Artist column
		TableColumn<Unit,String> artistColumn = new TableColumn<>("Artist");
		artistColumn.setMinWidth(100);
		artistColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getArtist());
		    }
		});
				
		//Album column
		TableColumn<Unit,String> albumColumn = new TableColumn<>("Album");
		albumColumn.setMinWidth(100);
		albumColumn.setCellValueFactory(new Callback<CellDataFeatures<Unit, String>, ObservableValue<String>>() {
		    @Override 
		    public ObservableValue<String> call(CellDataFeatures<Unit, String> c) {
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		        return new SimpleStringProperty(u.getSong().getAlbum());
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
		    	SingleSongUnit u = (SingleSongUnit) c.getValue();
		    	Duration d = u.getSong().getDuration();
		        return new SimpleStringProperty(Song.formatDuration(d));
		    }
		});		
		
		setItems(plist.getPlaylist());
		getColumns().addAll(titleColumn, artistColumn,albumColumn, durationColumn);
		
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
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	public Playlist getPlaylist() {
		return playlist;
	}
	
	
	public Button getPlaceholderButton(){
		return placeholder;
	}
	
	
}
