package controllers;

import java.io.FileWriter;
import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.CreationProcess;
import application.Main;
import background.CreatingVidBackgroundTask;
import customUI.ErrorAlert;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for video creation scene
 * @author Max Gurr & Jenna Kumar
 *
 */
public class CreatingVideoContoller extends SceneChanger { 
	private CreationProcess _creationProcess;
	private String _searchTerm;
	private String _fileName;
	
	@FXML
	private ProgressIndicator _progressIndicator;
	@FXML
	private Label _creatingLabel;
	@FXML
	private Button _cancelButton;
	@FXML
	private AnchorPane _anchorPane;

	@FXML
	/**
	 * Run when scene loads
	 */
	public void initialize() {
		_creationProcess = CreationProcess.getInstance();
		_fileName = _creationProcess.getFileName();
		_searchTerm = _creationProcess.getSearchTerm();
		
		createVideo();
	}

	/**
	 * Run background thread for video creation
	 */
	private void createVideo() { 
		//create video on background thread
		CreatingVidBackgroundTask createVidBG = new CreatingVidBackgroundTask();
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
			cancelCreation();
			
			try {
				changeScene(_anchorPane, "/fxml/MainMenuPane.fxml");
			} catch (IOException e) {
				new ErrorAlert("Couldn't change scene");
			}
		});

		//When creation has finished
		createVidBG.setOnSucceeded(succeeded -> {
			
			_cancelButton.setVisible(false);
			
			//Check for successful video creation
			if (createVidBG.getValue()) {
				//Store new creation information;
				storeInfo();
				
			} else {
				//Search failed, inform user and cancel process
				new ErrorAlert("Could not could not create video");
				
				cancelCreation();	
			}
			
			//Destroy instance
			CreationProcess.destroy();
			
			//Change to main menu
			try {
				changeScene(_anchorPane, "/fxml/MainMenuPane.fxml");
			} catch (IOException e) {
				new ErrorAlert("Couldn't change scene");
			}
		});
	}
	
	/**
	 * Store info about new creation in info file
	 */
	private void storeInfo() {
		try {
			//Get duration of new creation
			String lengthCommand = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + Main._CREATIONPATH + "/" + _fileName + Creation.EXTENTION;
			String length = BashCommandClass.getOutputFromCommand(lengthCommand).trim();
			double doubleLength = Double.parseDouble(length);
			String roundedLength = String.format("%.2f", doubleLength);
			String accuracy = "0";
			
			//Open creation info file 
			String infoFile = Main._FILEPATH + "/creationInfo.txt";
			FileWriter fw = new FileWriter(infoFile, true);
			
			//Write new info to it
			String newCreationInfo = _fileName + ";" + _searchTerm + ";" + roundedLength + ";" + accuracy + "\n";
			fw.write(newCreationInfo);
			fw.close();
			
			//Add new object to creation list
			Main.getCreationList().add(new Creation(_fileName, _searchTerm, roundedLength));
		} catch (IOException | InterruptedException e) {
			//Something went wrong, inform user and abort
			new ErrorAlert("Couldn't add new creation information");
			
			cancelCreation();
		}
	}
	
	/**
	 * Cancel creation, remove files
	 */
	private void cancelCreation() {
		//Remove files that had been created
		String audioFilePath = Main._AUDIOPATH + "/" + _fileName + Creation.AUDIO_EXTENTION;
		String slidesFilePath= Main._VIDPATH + "/" + _fileName  + Creation.EXTENTION;
		String creationFilePath = Main._CREATIONPATH + "/" + _fileName + Creation.EXTENTION;
		String removeVidFiles = "rm -f " + audioFilePath + " " + slidesFilePath + " " + creationFilePath;
		
		try {
			BashCommandClass.runBashProcess(removeVidFiles);
		} catch (IOException | InterruptedException e1) {
			new ErrorAlert("Could not quit");
		}
	}
}
