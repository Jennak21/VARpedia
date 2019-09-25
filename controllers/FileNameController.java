package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.BashCommandClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FileNameController extends SceneChanger implements Initializable{
	
	@FXML
	private Button _noButton;

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


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_yesButton.setVisible(false);
		_noButton.setVisible(false);
		_nextButton.setDisable(true);
		_creationProcess = CreationProcess.getInstance();
		
	}
	
	@FXML
	private void onYesButtonHandler(ActionEvent event) {
		deleteExistingFile(_fileName);
		_creationProcess.setFileName(_fileName);
	}
	
	@FXML
	private void onNoButtonHandler(ActionEvent event) {
		_fileNameEntry.clear();
		_overwriteLabel.setVisible(false);
		_yesButton.setVisible(false);
		_noButton.setVisible(false);
		_nextButton.setVisible(true);
		_nextButton.setDisable(true);
		
	}
	
	@FXML
	private void onBackButtonHandler(ActionEvent event) {

	}
	
	@FXML
	private void onTypeHandler() {
		//check if field properties are empty or not, set create button to disable or not accordingly
		_fileName = _fileNameEntry.getText();
		boolean isDisabled = ((_fileName.isEmpty())|| _fileName.trim().isEmpty());
		_nextButton.setDisable(isDisabled);
	

	}

	@FXML
	private void onNextButtonHandler(ActionEvent event) {
		if (fileExists(_fileName)) {
			_overwriteLabel.setVisible(true);
			_yesButton.setVisible(true);
			_noButton.setVisible(true);
			_nextButton.setVisible(false);
		} else {		
			_creationProcess.setFileName(_fileName);
		}


	}
	
	public void deleteExistingFile (String fileName) {
		String command = "rm ./creations/\"" + fileName + "\".mp4";
		try {
			BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public boolean fileExists(String fileName) {

		//run command to check if file name exists
		String command = "test -f creations/\"" + fileName + "\".mp4";
		int num;
		try {
			num = BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		//check exit code to determine if file exists
		if (num == 0 ) {		
			return true;
		} else {
			return false;
		}

	}

}
