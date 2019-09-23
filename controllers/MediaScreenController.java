package controllers;

import java.io.IOException;

import application.Creation;
import application.Main;
import application.MediaProcess;
import javafx.event.ActionEvent;
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

public class MediaScreenController extends SceneChanger {
	@FXML
	private StackPane _mediaStackPane;
	@FXML
	private BorderPane _controlsPane;
	@FXML
	private HBox _bottomControls;
	@FXML
	private Button _button;

	private MediaView view;
	private MediaPlayer player;
	
	@FXML
	private void initialize() {
		MediaProcess process = MediaProcess.getInstance();
		Creation creation = process.getCreation();
		String creationName = creation.getName();
		
		String fullFilePath = Main._FILEPATH + "/" + creationName + Creation.getExtention();
		
		Media media = new Media("file://" + fullFilePath);
		player = new MediaPlayer(media);
		view = new MediaView(player);
		
		
		_mediaStackPane.getChildren().clear();
		_mediaStackPane.getChildren().addAll(view, _controlsPane);
		
		player.play();
	}
	
	@FXML
	private void buttonHandle(ActionEvent event) throws IOException {
		player.stop();
		MediaProcess.destroyProcess();
		
		changeScene(event, "/fxml/MainMenuPane.fxml");
	}
}
