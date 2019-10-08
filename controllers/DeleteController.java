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
		
		_deleteLabel.setText("Are you sure you want to delete '" + _creation.getFilename() + "'?");
	}
	
	@FXML
	private void yesHandle() {
		String creationFile = Main._CREATIONPATH + "/" + _creation.getFilename() + Creation.EXTENTION;
		String videoFile = Main._VIDPATH + "/" + _creation.getFilename() + Creation.EXTENTION;
		String audioFile = Main._AUDIOPATH + "/" + _creation.getFilename() + Creation.EXTENTION;
		String deleteFiles = "rm " + creationFile + " " + videoFile + " " + audioFile;
		System.out.println(deleteFiles);
		
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
