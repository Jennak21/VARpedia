package controllers;

import java.io.IOException;
import java.util.List;

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
	private String _filename;
	
	@FXML
	private void initialize() {
		_creation = CreationStore.getInstance().getCreation();
		_filename = _creation.getFilename();
		
		_deleteLabel.setText("Are you sure you want to delete '" + _creation.getFilename() + "'?");
	}
	
	@FXML
	private void yesHandle() {
		deleteFiles();
		deleteCreationInfo();
		
		quit();
	}
	
	private void deleteFiles() {
		try {
			String creationFile = Main._CREATIONPATH + "/" + _filename + Creation.EXTENTION;
			String videoFile = Main._VIDPATH + "/" + _filename + Creation.EXTENTION;
			String audioFile = Main._AUDIOPATH + "/" + _filename + Creation.AUDIO_EXTENTION;
			String deleteFiles = "rm " + creationFile + " " + videoFile + " " + audioFile;
			
			int exitVal;
			exitVal = BashCommandClass.runBashProcess(deleteFiles);
			
			if (exitVal != 0) {
				new ErrorAlert("Couldn't delete file");
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete file");
		}
	}
	
	private void deleteCreationInfo() {
		try {
			String getCreationInfoFromFile = "cat " + Main._CREATIONINFO;
			List<String> creationInfo = BashCommandClass.getListOutput(getCreationInfoFromFile);
			
			String newFileInformation = "";
			
			for (String s: creationInfo) {
				String[] info = s.split(";");
				
				String filename = info[0];
				String searchTerm = info[1];
				String length = info[2];
				String testAcc = info[3];
				
				if (!_filename.equals(filename)) {
					newFileInformation = newFileInformation + s + "\n";
				}
			}
			
			String writeResultsToFile = "echo \"" + newFileInformation + "\" > " + Main._CREATIONINFO;
			BashCommandClass.runBashProcess(writeResultsToFile);
			
			Main.deleteCreation(_filename);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete file");
		}
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
