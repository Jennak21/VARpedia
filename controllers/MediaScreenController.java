package controllers;

import java.io.IOException;

import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Creation player - For combined full video + audio
 * @author Max Gurr
 * 
 */
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
	/**
	 * Run when scene loads
	 */
	private void initialize() {
		//Get filepath to creation media
		CreationStore process = CreationStore.getInstance();
		Creation creation = process.getCreation();
		String creationName = creation.getFilename();
		String fullFilePath = Main._CREATIONPATH + "/" + creationName + Creation.EXTENTION;
		
		//Setup player and load video
		_player = new VideoPlayer(_mediaStackPane, _controlsPane, _bottomControls);
		_player.setMedia(fullFilePath);
		_player.setAutoSize();
		
		_player.play();
	}
	
	@FXML
	/**
	 * Go back to main menu
	 */
	private void BackHandle(ActionEvent event) {
		//Stop video and destroy creation process
		_player.stopVideo();
		CreationStore.destroy();
		
		//Load main menu
		try {
			changeScene((Node)event.getSource(), "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't return to main menu");
		}
	}
}
