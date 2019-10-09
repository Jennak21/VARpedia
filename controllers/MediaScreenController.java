package controllers;

import java.io.IOException;

import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MediaScreenController extends SceneChanger {
	@FXML
	private StackPane _mediaStackPane;
	@FXML
	private BorderPane _controlsPane;
	@FXML
	private HBox _bottomControls;
	@FXML
	private Button _backButton;
	
	private VideoPlayer _player;

	
    		
	@FXML
	private void initialize() {
		//Setup player and load video
		CreationStore process = CreationStore.getInstance();
		Creation creation = process.getCreation();
		String creationName = creation.getFilename();
		String fullFilePath = Main._CREATIONPATH + "/apple" + Creation.EXTENTION;
		
		_player = new VideoPlayer(_mediaStackPane, _controlsPane, _bottomControls);
		_player.setMedia(fullFilePath);
		_player.setAutoSize();
		
		_player.play();
	}
	
	
	@FXML
	private void BackHandle(ActionEvent event) {
		_player.stopVideo();
		CreationStore.destroy();
		
		try {
			changeScene((Node)event.getSource(), "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't return to main menu");
		}
	}
}
