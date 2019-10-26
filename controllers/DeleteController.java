package controllers;

import java.io.IOException;

import application.Creation;
import application.ErrorAlert;
import background.DeleteCreationBackgroundTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Controller for delete creation scene
 * @author Max Gurr & Jenna Kumar
 *
 */
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
	/**
	 * Run when scene loads
	 */
	private void initialize() {
		_creation = CreationStore.getInstance().getCreation();
		_filename = _creation.getFilename();
		
		_deleteLabel.setText("Are you sure you want to delete '" + _creation.getFilename() + "'?");
	}
	
	@FXML
	/**
	 * Delete file
	 */
	private void yesHandle() {
		//Setup and run new background method
		DeleteCreationBackgroundTask deleteCreation = new DeleteCreationBackgroundTask(_filename);
		Thread deleteThread = new Thread(deleteCreation);
		deleteThread.start();
		
		//When process starts, disable buttons
		deleteCreation.setOnRunning(start -> {
			_yesButton.setDisable(true);
			_noButton.setDisable(true);
		});
		
		//When process finishes, go back to main menu
		deleteCreation.setOnSucceeded(finish -> {
			if (!deleteCreation.getValue()) {
				//Deletion failed, notify user
				new ErrorAlert("Could not delete files");
			}
			
			//Exit scene
			quit();
		});
	}
	
	@FXML
	/**
	 * Don't delete file
	 */
	private void noHandle() {
		//Exit scene
		quit();
	}
	
	/**
	 * Quit back to menu
	 */
	private void quit() {
		//Destroy creation store
		CreationStore.destroy();
		
		//Load menu scene
		try {
			changeScene(_gridPane, "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}
	}
}
