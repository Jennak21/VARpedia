package controllers;

import java.io.IOException;

import application.Creation;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class MediaScreenController {
	@FXML
	private StackPane mediaStackPane;
	@FXML
	private BorderPane controlsPane;
	@FXML
	private HBox bottomControls;
	@FXML
	private Button button;

	private MediaView view;
	private MediaPlayer player;
	
	@FXML
	private void initialize() {
		String fullFilePath = Main.FILEPATH + "/jesus.mp4";
		
		Media media = new Media("file://" + fullFilePath);
		player = new MediaPlayer(media);
		view = new MediaView(player);
		
		
		mediaStackPane.getChildren().clear();
		mediaStackPane.getChildren().addAll(view, controlsPane);
		
		player.play();
	}
	
	@FXML
	private void buttonHandle() throws IOException {
		player.stop();
		
		Stage stage = (Stage) mediaStackPane.getScene().getWindow();
		Parent layout = FXMLLoader.load(getClass().getResource("/fxml/MainMenuPane.fxml"));
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}
}
