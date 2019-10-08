package controllers;

import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class DeleteController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
	@FXML
	private Label _deleteLabel;
	@FXML
	private Button _yesButton;
	@FXML
	private Button _noButton;
	
	private Creation _creation;
	
	@FXML
	private void initialize() {
		_creation = CreationStore.getInstance().getCreation();
		
		_deleteLabel.setText("Are you sure you want to delete '" + _creation.getName() + "'?");
	}
	
	@FXML
	private void yesHandle() {
		String deleteCreation = "rm " + Main._CREATIONPATH + "/" + _creation.getName() + Creation.EXTENTION + "; ";
		String deleteAudio = "rm " + Main._AUDIOPATH + "/" + _creation.getName() + Creation.AUDIO_EXTENTION + "; ";
		String deleteVideo = "rm " + Main._VIDPATH + "/" + _creation.getName() + Creation.EXTENTION + "; ";
		
		String deleteFiles = deleteCreation + deleteAudio + deleteVideo;
		
		try {
			int exitVal;
			exitVal = BashCommandClass.runBashProcess(deleteFiles);
			
			if (exitVal != 0) {
				new ErrorAlert("Couldn't delete file");
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete file");
		}
		
		quit();
	}
	
	@FXML
	private void noHandle() {
		quit();
	}
	
	private void quit() {
		CreationStore.destroy();
		
		try {
			changeScene(_gridPane, "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}
	}
}
