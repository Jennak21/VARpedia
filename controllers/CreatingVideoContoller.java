package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import background.CreatingVidBackgroundTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.Initializable;


public class CreatingVideoContoller extends SceneChanger implements Initializable{ 

	private CreationProcess _creationProcess;
	private String _searchTerm;
	private int _numImages;
	private String _fileName;
	private String _filePath = Main._FILEPATH +"/newCreation/";
	private String _tempFilePath = Main._FILEPATH +"/newCreation/vidCreationTemp/";
	private String _tempAudioFilePath;
	private String _tempSlidesFilePath;
	private String _tempVidFilePath;
	private String _creationFilePath;
	
	@FXML
	private ProgressIndicator _progressIndicator;

	@FXML
	private Label _creatingLabel;
	@FXML
	private Button _cancelButton;
	@FXML
	private AnchorPane _anchorPane;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_creationProcess = CreationProcess.getInstance();
		_searchTerm = _creationProcess.getSearchTerm();
		_numImages = _creationProcess.getNumImages();
		_fileName= "\"" + _creationProcess.getFileName() + "\"";

		//setup paths
		_tempAudioFilePath = _tempFilePath + _fileName + "TempAudio" + Creation.AUDIO_EXTENTION ;
		_tempSlidesFilePath= _tempFilePath + _fileName + "TempSlide" + Creation.EXTENTION;
		_tempVidFilePath = _tempFilePath  + _fileName + "TempVideo" + Creation.EXTENTION;
		_creationFilePath = Main._FILEPATH + "/" + _fileName + Creation.EXTENTION;
		
		createVideo();

	}


	private void createVideo() { 


		//create video on background thread
		CreatingVidBackgroundTask createVidBG = new CreatingVidBackgroundTask(_searchTerm, _numImages, _filePath, _tempFilePath, _tempAudioFilePath, _tempSlidesFilePath, _tempVidFilePath, _creationFilePath);
		Thread createVidThread = new Thread(createVidBG );
		createVidThread.start();



		createVidBG.setOnRunning(running -> {
			//make label visible which will show progress updates
			_creatingLabel.textProperty().bind( createVidBG.messageProperty());
			_creatingLabel.setVisible(true);
			
			_progressIndicator.progressProperty().unbind();
			_progressIndicator.progressProperty().bind(createVidBG.progressProperty());

			//Cancel creation if user wants to quit
			_cancelButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					createVidBG.cancel();;
				}
			});
		});

		//If user has cancelled, go back to back to fileName screen
		createVidBG.setOnCancelled(cancel -> {
			quit();
		});

		//When creation has finished
		createVidBG.setOnSucceeded(succeeded -> {
			
			_cancelButton.setVisible(false);
			
			//Check for successful video creation
			if (createVidBG.getValue()) {
				try {
					changeScene(_anchorPane, "/fxml/MainMenuPane.fxml");
				} catch (IOException e) {
					new ErrorAlert("Couldn't change scene");
				}

			} else {
				//Search failed, inform user and go back to search screen
				new ErrorAlert("Could not could not create video");
				try {
					changeScene(_anchorPane, "/fxml/MainMenuPane.fxml");
				} catch (IOException e) {
					new ErrorAlert("Couldn't change scene");
				}
			}
			
			CreationProcess.destroy();
		});
	}
	
	private void quit() {
		// remove files that had been created
		String removeVidFiles = "rm -r " + _tempFilePath;
		try {
			BashCommandClass.runBashProcess(removeVidFiles);
		} catch (IOException | InterruptedException e1) {
			new ErrorAlert("Could not quit");
		}
		try {
			changeScene(_anchorPane, "/fxml/SelectAudioScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}



}
