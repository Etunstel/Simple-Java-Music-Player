import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class NewPlaylistPrompt {

	private static String chosenTitle = null;
	
	public static String display() {	
		Stage window = new Stage();
		
		String s = "New Playlist";
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(s);
		window.setMinWidth(250);
		
		
		Label label = new Label("Name");
		TextField playlistName = new TextField("New Playlist");
		
		Button cancel = new Button("Cancel");
		Button confirm = new Button("Create");
		cancel.setOnAction(e-> window.close());
		confirm.setOnAction(e-> {
			if(validateTitle(playlistName, playlistName.getText())) {
				chosenTitle = playlistName.getText();
				window.close();
			} 
		});

		VBox layout = new VBox(10);
		
		layout.setAlignment(Pos.CENTER);
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(cancel, confirm);
		
		layout.getChildren().addAll(label,playlistName, buttons);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		
		return chosenTitle;
	}
	
	
	public static boolean validateTitle(TextField a, String title) {
		if (title.length() <= 100) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
