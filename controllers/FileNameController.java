package controllers;

import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import background.DeleteCreationBackgroundTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Controller for filename scene
 * @author Max Gurr & Jenna Kumar
 *
 */
public class FileNameController extends SceneChanger {
	@FXML
	private Button _noButton;
	@FXML
	private GridPane _gridPane;
	@FXML
	private Button _yesButton;
	@FXML
	private Button _nextButton;
	@FXML
	private Button _backButton;
	@FXML
	private Label _overwriteLabel;
	@FXML
	private TextField _fileNameEntry;
	
	private CreationProcess _creationProcess;
	private String _fileName;
	private String _filePath = Main._CREATIONPATH;

	@FXML
	/**
	 * Run when scene loads
	 */
	private void initialize() {
		//make buttons used for future prompts invisible
		_yesButton.setVisible(false);
		_noButton.setVisible(false);
		
		_nextButton.setDisable(true);
		
		//make mouse focus on search text area
		Platform.runLater(()->_fileNameEntry.requestFocus());

		_creationProcess = CreationProcess.getInstance();		
	}
	
	@FXML
	/**
	 * Overwrite existing creation with same filename
	 */
	private void onYesButtonHandler(ActionEvent event) {
		//if user wants to replace delete existing and store file name
		DeleteCreationBackgroundTask deleteCreation = new DeleteCreationBackgroundTask(_fileName);
		Thread deleteThread = new Thread(deleteCreation);
		deleteThread.start();
		
		deleteCreation.setOnRunning(start -> {
			_yesButton.setDisable(true);
			_noButton.setDisable(true);
			_nextButton.setDisable(true);
			_backButton.setDisable(true);
		});
		
		//If deletion is successful change scene, otherwise show alert
		deleteCreation.setOnSucceeded(finish -> {
			if (deleteCreation.getValue()) {
				_creationProcess.setFileName(_fileName);
				
				changeToCreateVideoScene();
			} else {
				new ErrorAlert("Could not delete files");
				initialize();
			}
		});
	}
	
	@FXML
	/**
	 * Don't overwrite existing creation
	 */
	private void onNoButtonHandler(ActionEvent event) {
		//clear text input for filename
		_fileNameEntry.clear();
		_fileNameEntry.setDisable(false);
		
		//set overwriting buttons and text to invisible
		_overwriteLabel.setVisible(false);
		_yesButton.setVisible(false);
		_noButton.setVisible(false);
		
		_nextButton.setVisible(true);
		_nextButton.setDisable(true);
	}
	
	@FXML
	/**
	 * Go back to image selection scene
	 */
	private void onBackButtonHandler(ActionEvent event) {
		try {
			changeScene((Node)event.getSource(), "/fxml/SelectImageScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}

	}
	
	@FXML
	/**
	 * When user types
	 */
	private void onTypeHandler(KeyEvent event) {
		//remove trailing white spaces and replace other white spaces with underscore
		_fileName = _fileNameEntry.getText().trim().replaceAll(" ", "_");
		
		//check if the fileName is empty or not, then set create button to  be disabled or not accordingly
		boolean isDisabled = (_fileName.isEmpty());
		_nextButton.setDisable(isDisabled);
		
		//if user presses enter, and fileName is not empty  then save file
		if ((event.getCode() == KeyCode.ENTER) && !_fileName.isEmpty()) {
	        saveFileName();
	    }
	}
	
	@FXML
	/**
	 * Save and process filename
	 */
	private void onNextButtonHandler(ActionEvent event) {	
		saveFileName();
	}
	
	/**
	 * Save entered filename
	 */
	public void saveFileName() {
		//if file exists show the confirm overwrite label and buttons
		if (fileExists()) {
			_fileNameEntry.setDisable(true);
			_overwriteLabel.setVisible(true);
			_yesButton.setVisible(true);
			_noButton.setVisible(true);
			_nextButton.setVisible(false);
		} else {		
			_creationProcess.setFileName(_fileName);
			changeToCreateVideoScene();
		}	
	}

	/**
	 * Check whether entered filename already exists
	 * @param fileName - File to check for
	 * @return boolean - Whether or not filename exists
	 */
	public boolean fileExists() {
		//run command to check if file name exists
		String command = "test -f " + _filePath + "/\"" + _fileName + "\"" + Creation.EXTENTION;
		int num;
		try {
			num = BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
			return false;
		}

		//check exit code to determine if file exists
		if (num == 0 ) {		
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Go to final video creation process scene
	 */
	public void changeToCreateVideoScene () {
		try {
			changeScene(_gridPane, "/fxml/CreatingVideoScene.fxml") ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
