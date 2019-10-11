package controllers;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import application.WarningAlert;
import background.AudioBackgroundTask;
import background.PlayAudioBackgroundTask;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class CreateAudioController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
	@FXML
	private GridPane _settingsGrid;
	@FXML
	private GridPane _filenameGrid;
	@FXML
	private TextArea _searchResult;
	@FXML
	private Button _resetButton;
	@FXML
	private Button _nextButton;
	@FXML
	private Button _backButton;
	@FXML
	private ChoiceBox _voiceDropDown;
	@FXML
	private Button _previewButton;
	@FXML
	private Button _saveButton;
	@FXML
	private TextArea _savedText;
	@FXML
	private Button _listenButton;
	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;
	@FXML
	private TextArea _promptText;
	
	private CreationProcess _process;
	private PlayAudioBackgroundTask _preview;
	private String _selectedText;
	private String _selectedVoice;
	
	/**
	 * Run when scene is setup
	 */
	@FXML
	public void initialize() {
		
		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		
		//Set search text
		_process = CreationProcess.getInstance();
		_searchResult.setText(_process.getSearchText());
		
		//Get available festival voices and set in dropdown
		List<String> voices = new ArrayList<String>();
		ObservableList<String> voiceChoices = FXCollections.observableArrayList();
		
		try {
			String getVoices = "ls /usr/share/festival/voices/english";
			voices = BashCommandClass.getListOutput(getVoices);
			for (String s: voices) {
				voiceChoices.add(s);
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't get voices");
		}
		
		_voiceDropDown.setItems(voiceChoices);
		_voiceDropDown.getSelectionModel().select(3);;
		
		//Check if any audio files exist, if not then disable 'next' button
		try {
			String checkAudio = "ls " + Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
			int exitVal = BashCommandClass.runBashProcess(checkAudio);
			
			if (exitVal != 0) {
				_nextButton.setDisable(true);
				_listenButton.setDisable(true);
			}
		} catch (IOException | InterruptedException e) {
			
		}

		_savedText.setText(_process.getUserText());;
	}
	
	@FXML
	private void helpHandle(ActionEvent event) {
		_helpPane.setVisible(true);
		_helpText.setVisible(true);
		_helpButton.setVisible(false);

	}
	
	@FXML
	private void exitHelpHandle(ActionEvent event) {
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		_helpButton.setVisible(true);

	}
	
	@FXML
	private void onTextClickHandle(MouseEvent event) {
		//remove prompt text
		_promptText.setVisible(false);
	}
	
	
	/**
	 * Reset search text or audio file
	 */
	@FXML
	private void resetHandle() {
		if (_resetButton.getText().equals("Reset Audio")) {
			try {
				String searchCommand = "ls " + Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
				int exitVal = BashCommandClass.runBashProcess(searchCommand);
				
				if (exitVal == 0) {
					String resetCommand = "rm -f " + Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
					exitVal = BashCommandClass.runBashProcess(resetCommand);
					
					if (exitVal == 0) {
						new InformationAlert("Audio reset");
						_process.resetUserText();
						_nextButton.setDisable(true);
						_savedText.setText(_process.getUserText());
					} else {
						new ErrorAlert("Could not reset audio");
					}
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (_resetButton.getText().equals("Reset Text")) {
			_searchResult.setText(_process.getSearchText());
		}
	}
	
	/**
	 * Run for preview text
	 */
	@FXML
	private synchronized void previewHandle() {
		//Action dependent on whether there is a preview running or not
		if (_previewButton.getText().equals("Preview Selected Text")) {
			if (_preview != null) {
				_preview.stopProcess();
			}
			
			//Create files for preview text and settings
			if (createTextFile() && createSettingsFile()) {
				//Background task for making audio file
				AudioBackgroundTask createAudio = new AudioBackgroundTask("tempAudio");
				Thread audioThread = new Thread(createAudio);
				audioThread.start();
				
				//When audio finished being made
				createAudio.setOnSucceeded(finish -> {
					//If successfully made audio, play it
					if (createAudio.getValue()) {
						playAudio();
					} else {
						new ErrorAlert("Couldn't play audio");
					}
				});
			}
		} else {
			if (_preview != null) {
				_preview.stopProcess();
			}
		}			

	}
	
	private void playAudio() {
		_preview = new PlayAudioBackgroundTask("tempAudio", Main._FILEPATH + "/newCreation/");
		Thread previewThread = new Thread(_preview);
		previewThread.start();
		
		_preview.setOnRunning(run -> {
			_previewButton.setText("Stop preview");
		});
		
		_preview.setOnSucceeded(finish -> {
			_previewButton.setText("Preview Selected Text");
			try {			
				String removeFile = "rm -f " + Main._FILEPATH + "/newCreation/tempAudio" + Creation.AUDIO_EXTENTION;
				BashCommandClass.runBashProcess(removeFile);
			} catch (IOException | InterruptedException e) {
				new ErrorAlert("Something went wrong");
			}
		});
	}
	
	@FXML
	private void saveHandle() {		
		if (_saveButton.getText().equals("Save Audio")) {
			if (createTextFile() && createSettingsFile()) {
				_saveButton.setText("Saving...");
				
				//Background task for making audio file
				AudioBackgroundTask createAudio = new AudioBackgroundTask("newAudio");
				Thread audioThread = new Thread(createAudio);
				audioThread.start();
				
				//When new audio finished being saved
				createAudio.setOnSucceeded(finish -> {
					//If successfully saved audio
					if (createAudio.getValue()) {
						boolean combineVal = combineAudio();
						if (combineVal) {
							new InformationAlert("Added new audio");
							
							_process.addToUserText(_selectedText + "\n");
							_savedText.setText(_process.getUserText());
							
							_nextButton.setDisable(false);
							_listenButton.setDisable(false);
						} 
						//Couldn't save new audio
						else {
							new ErrorAlert("Couldn't add audio");
						}
					} else {
						new ErrorAlert("Couldn't save audio");
					}
					
					_saveButton.setText("Save Audio");
				});
			}
		}
	}
	
	private boolean combineAudio() {
		try {
			//Check if there is existing audio
			String checkForAudio = "ls " + Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
			int exitVal = BashCommandClass.runBashProcess(checkForAudio);
			
			//Audio file exists
			if (exitVal == 0) {
				String file1 = Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
				String file2 = Main._FILEPATH + "/newCreation/newAudio" + Creation.AUDIO_EXTENTION;
				String outputFile = Main._FILEPATH + "/newCreation/finalAudio" + Creation.AUDIO_EXTENTION;
				String combineAudioCommand = "sox " + file1 + " " + file2 + " " + outputFile;
			
				exitVal = BashCommandClass.runBashProcess(combineAudioCommand);
				
				if (exitVal == 0) {
					String removeCommand = "rm -f " + file1 + " " + file2;
					BashCommandClass.runBashProcess(removeCommand);
					
					File tempAudio = new File(Main._FILEPATH + "/newCreation/finalAudio" + Creation.AUDIO_EXTENTION);
			        File finalAudio = new File(Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION);
			 
			        // Renames the file denoted by this abstract pathname.
			        tempAudio.renameTo(finalAudio);
			        
			        return true;
				} else {
					String removeCommand = "rm -f " + file2;
					BashCommandClass.runBashProcess(removeCommand);
										
					return false;
				}
			} 
			//Audio file doesn't exist
			else {
				File tempAudio = new File(Main._FILEPATH + "/newCreation/newAudio" + Creation.AUDIO_EXTENTION);
		        File finalAudio = new File(Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION);
		 
		        // Renames the file denoted by this abstract pathname.
		        tempAudio.renameTo(finalAudio);
		        
		        return true;
			}
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	private boolean createSettingsFile() {
		_selectedVoice = (String)_voiceDropDown.getSelectionModel().getSelectedItem();

		if (_selectedVoice != null && !_selectedVoice.isEmpty()) {
			try {
				String voiceSetting = "(voice_" + _selectedVoice + ")";
				String settingsFile = "echo \"" + voiceSetting + "\" > " + Main._FILEPATH + "/newCreation/settings.scm";
				BashCommandClass.runBashProcess(settingsFile);
				
				return true;
			} catch (IOException | InterruptedException e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean createTextFile() {
		_selectedText = _searchResult.getSelectedText();
		
		boolean valid = true;
		
		if (_selectedText == null || _selectedText.isEmpty()) {
			valid = false;
		}

		String[] words = _selectedText.split("\\s+");
		int length = words.length;
		
		if (length > 40) {
			valid = false;
		}
		
		if (valid) {
			try {
				String textFile = "echo \"" + _selectedText + "\" > " + Main._FILEPATH + "/newCreation/text.txt";
				BashCommandClass.runBashProcess(textFile);
				
				return true;
			} catch (IOException | InterruptedException e) {
				return false;
			}
		} else {
			new WarningAlert("Please select between 1-40 words");
			return false;
		}
	}	
	
	@FXML
	private void backHandle() {		
		try {
			String removeFolder = "rm -r " + Main._FILEPATH + "/newCreation";
			BashCommandClass.runBashProcess(removeFolder);
			
			changeScene(_gridPane, "/fxml/SearchScene.fxml");
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
		}
	}
	
	@FXML
	private void nextHandle() {	
		
		//Disable for testing
//		storeInfo();
		
			try {
				String removeTempaudio = "rm -f " + Main._FILEPATH + "/newCreation/tempAudio" + Creation.AUDIO_EXTENTION;
				BashCommandClass.runBashProcess(removeTempaudio);
				
				changeScene(_gridPane, "/fxml/SelectImageScene.fxml");
			} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}
	
	
	//Method for testing purposes only
	private void storeInfo() {
		
		try {
			String filename = "newCreation.mp3";
			String searchTerm = _process.getSearchTerm();
			
			String lengthCommand = "echo `soxi -D " + Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION + "`";
			String length = BashCommandClass.getOutputFromCommand(lengthCommand);
			
			String newCreationInfo = "\n" + filename + ";" + searchTerm + ";" + length + ";" + "0";
			
			String addInfo = "echo \"$(cat " + Main._FILEPATH + "/creationInfo.txt) " + newCreationInfo + "\" > " + Main._FILEPATH + "/creationInfo.txt";
			BashCommandClass.runBashProcess(addInfo);
			
			Main.getCreationList().add(new Creation(filename, searchTerm, length));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void listenHandle() {
		if (_listenButton.getText().equals("Listen")) {
			if (_preview != null) {
				_preview.stopProcess();
			}
			
			_preview = new PlayAudioBackgroundTask("audio", Main._FILEPATH + "/newCreation/");
			Thread previewThread = new Thread(_preview);
			previewThread.start();
			
			_preview.setOnRunning(run -> {
				_listenButton.setText("Stop");
			});
			
			_preview.setOnSucceeded(finish -> {
				_listenButton.setText("Listen");
			});
		} else {
			if (_preview != null) {
				_preview.stopProcess();
			}
		}
	}
	
	@FXML
	private void resultTabHandle() {
		_resetButton.setText("Reset Text");
	}
	
	@FXML
	private void savedTabHandle() {
		_resetButton.setText("Reset Audio");
	}
}
