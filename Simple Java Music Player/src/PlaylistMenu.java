// Sidebar list that allows the user to click on a playlist's title and display/interact with it
import javafx.scene.control.*;

import javafx.collections.*;



public class PlaylistMenu extends ListView<Playlist>{
	
	
	public PlaylistMenu(){
		minWidth(100);
	}
	
	
	public PlaylistMenu(ObservableList<Playlist> plists) {
		minWidth(100);
		
		setCellFactory(listview ->{
			return new ListCell<Playlist>() {
				@Override
				public void updateItem(Playlist item, boolean empty) {
					super.updateItem(item, empty);
					textProperty().unbind();
					setText((empty || item == null) ? null : item.getTitle());
				}
			};
		}
		);
		
		setItems(plists);
		
	}
	
	

}
