package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import application.WarningAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class FileNameController extends SceneChanger implements Initializable{
	
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


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//make buttons used for future prompts invisible
		_yesButton.setVisible(false);
		_noButton.setVisible(false);
		
		_nextButton.setDisable(true);
		
		_creationProcess = CreationProcess.getInstance();
		
	}
	
	@FXML
	private void onYesButtonHandler(ActionEvent event) {
		//if user wants to replace delete existing and store file name
//		deleteExistingFile(_fileName);
		_creationProcess.setFileName(_fileName);
		
		changeToCreateVideoScene();
	}
	
	@FXML
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
	private void onBackButtonHandler(ActionEvent event) {
		try {
			changeScene((Node)event.getSource(), "/fxml/SelectImageScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}

	}
	
	@FXML
	private void onTypeHandler(KeyEvent event) {
		//check if field properties are empty or not, set create button to  be disabled or not accordingly
		_fileName = _fileNameEntry.getText().trim();
		boolean isDisabled = (_fileName.isEmpty());
		_nextButton.setDisable(isDisabled);
		
		if ((event.getCode() == KeyCode.ENTER) && !_fileName.isEmpty() && !_fileName.trim().isEmpty()) {
	        saveFileName();
	    }

	}
	
	

	@FXML
	private void onNextButtonHandler(ActionEvent event) {	
		saveFileName();
	}
	
	public void saveFileName() {
		if (fileExists(_fileName)) {
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
	
	public void deleteExistingFile (String fileName) {
		String command = "rm " + _filePath + "/\"" + fileName + "\"" + Creation.EXTENTION;
		try {
			BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			new WarningAlert("Could not delete existing file");
		}
	}

	
	public boolean fileExists(String fileName) {

		//run command to check if file name exists
		String command = "test -f " + _filePath + "/\"" + fileName + "\"" + Creation.EXTENTION;
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
	
	public void changeToCreateVideoScene () {
		try {
			changeScene(_gridPane, "/fxml/CreatingVideoScene.fxml") ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
