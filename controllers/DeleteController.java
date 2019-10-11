package controllers;

import java.io.IOException;
import java.util.List;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import background.DeleteCreationBackgroundTask;
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
	private String _filename;
	
	@FXML
	private void initialize() {
		_creation = CreationStore.getInstance().getCreation();
		_filename = _creation.getFilename();
		
		_deleteLabel.setText("Are you sure you want to delete '" + _creation.getFilename() + "'?");
	}
	
	@FXML
	private void yesHandle() {
		DeleteCreationBackgroundTask deleteCreation = new DeleteCreationBackgroundTask(_filename);
		Thread deleteThread = new Thread(deleteCreation);
		deleteThread.start();
		
		deleteCreation.setOnRunning(start -> {
			_yesButton.setDisable(true);
			_noButton.setDisable(true);
		});
		
		deleteCreation.setOnSucceeded(finish -> {
			if (!deleteCreation.getValue()) {
				new ErrorAlert("Could not delete files");
			}
			
			quit();
		});
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
