package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import application.WarningAlert;
import background.AudioBackgroundTask;
import background.PreviewBackgroundTask;
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
import javafx.scene.layout.GridPane;

public class CreateAudioController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
	@FXML
	private GridPane _settingsGrid;
	@FXML
	private GridPane _filenameGrid;
	@FXML
	private TextArea _textArea;
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
	private TextField _filenameField;
	@FXML
	private Button _cancelButton;
	@FXML
	private Button _makeAudioButton;
	@FXML
	private GridPane _nameEntryGrid;
	@FXML
	private GridPane _overwriteGrid;
	@FXML
	private Button _yesButton;
	@FXML
	private Button _noButton;
	@FXML
	private Label _overwriteLabel;
	
	private CreationProcess _process;
	private PreviewBackgroundTask _preview;
	private String _selectedText;
	private String _selectedVoice;
	private String _filename;
	
	/**
	 * Run when scene is setup
	 */
	@FXML
	public void initialize() {
		//Set search text
		_process = CreationProcess.getInstance();
		_textArea.setText(_process.getSearchText());
		
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
		_voiceDropDown.getSelectionModel().selectFirst();
		
		//Check if any audio files exist, if not then disable 'next' button
		try {
			String checkAudio = "ls " + Main._FILEPATH + "/newCreation/*" + Creation.AUDIO_EXTENTION;
			int exitVal = BashCommandClass.runBashProcess(checkAudio);
			
			if (exitVal != 0) {
				_nextButton.setDisable(true);
			}
		} catch (IOException | InterruptedException e) {
			
		}
		
		//Ensure scene is at initial state
		ResetScene();
	}
	
	/**
	 * Reset search text
	 */
	@FXML
	private void ResetHandle() {
		_textArea.setText(_process.getSearchText());
	}
	
	/**
	 * Run for preview text
	 */
	@FXML
	private synchronized void PreviewHandle() {
		//Action dependent on whether there is a preview running or not
		if (_previewButton.getText().equals("Preview Selected Text")) {
			if (createTextFile() && createSettingsFile()) {
				AudioBackgroundTask createAudio = new AudioBackgroundTask("tempAudio");
				Thread audioThread = new Thread(createAudio);
				audioThread.start();
				
				createAudio.setOnSucceeded(finish -> {
					if (createAudio.getValue()) {
						try {
							String convert2mp3 = "ffmpeg -y -i " + Main._FILEPATH + "/newCreation/tempAudio.wav " + Main._FILEPATH + "/newCreation/tempAudio.mp4";
							int exitVal = BashCommandClass.runBashProcess(convert2mp3);
							
							if (exitVal == 0) {
								playAudio();
							} else {
								new ErrorAlert("Couldn't make audio");
								ResetScene();
							}
							
						} catch (IOException | InterruptedException e) {
							new ErrorAlert("Couldn't play audio");
						}
					} else {
						new ErrorAlert("Couldn't play audio");
						ResetScene();
					}
				});
			}
		} else {
			ResetScene();
		}
	}
	
	private void playAudio() {
		_preview = new PreviewBackgroundTask("tempAudio");
		Thread previewThread = new Thread(_preview);
		previewThread.start();
		
		_preview.setOnRunning(run -> {
			_previewButton.setText("Stop preview");
		});
		
		_preview.setOnSucceeded(finish -> {
			_previewButton.setText("Preview Selected Text");
		});
	}
	
	@FXML
	private void SaveHandle() {		
		if (createTextFile() && createSettingsFile()) {
			createTextFile();
			createSettingsFile();
			
			FilenameGrid();
		}
	}
	
	private void FilenameGrid() {
		_settingsGrid.setVisible(false);
		_filenameGrid.setVisible(true);
		_nameEntryGrid.setVisible(true);
		_overwriteGrid.setVisible(false);
	}
	
	@FXML
	private void MakeAudioHandle() {
		_filename = _filenameField.getText();
		if (_filename.isEmpty()) {
			new WarningAlert("Please enter a filename");
			return;
		}
		
		try {
			String fileExist = "ls " + Main._FILEPATH + "/newCreation/" + _filename + Creation.AUDIO_EXTENTION;
			int exitVal = BashCommandClass.runBashProcess(fileExist);
			

			if (exitVal != 0) {
				YesHandle();
			} else {
				_overwriteLabel.setText("'" + _filename + "' already exists, would you like to overwrite?");
				
				_nameEntryGrid.setVisible(false);
				_overwriteGrid.setVisible(true);
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
			ResetScene();
		}
	}
	
	private boolean createSettingsFile() {
		_selectedVoice = (String)_voiceDropDown.getSelectionModel().getSelectedItem();

		if (_selectedVoice != null && !_selectedVoice.isEmpty()) {
			try {
				String settingsFile = "echo \"(voice_" + _selectedVoice + ")\" > " + Main._FILEPATH + "/newCreation/settings.scm";
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
		_selectedText = _textArea.getSelectedText();
		
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
	private void YesHandle() {
		AudioBackgroundTask audioTask = new AudioBackgroundTask(_filename);
		Thread audioThread = new Thread(audioTask);
		audioThread.start();
		
		audioTask.setOnRunning(running -> {
			_cancelButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					audioTask.cancel();
				}
			});
		});
		
		audioTask.setOnCancelled(cancel -> {
			ResetScene();
		});
		
		audioTask.setOnSucceeded(finish -> {
			if (audioTask.getValue()) {
				_nextButton.setDisable(false);
				new InformationAlert("Successfully created audio");
			} else {
				new ErrorAlert("Couldn't create audio");
			}
			
			ResetScene();
		});
	}
	
	@FXML
	private void NoHandle() {
		FilenameGrid();
	}
	
	@FXML
	private void CancelHandle() {
		ResetScene();
	}
	
	private void ResetScene() {
		_filenameField.clear();
		_settingsGrid.setVisible(true);
		_filenameGrid.setVisible(false);
		_previewButton.setText("Preview Selected Text");
		
		if (_preview != null) {
			_preview.stopProcess();
		}
		
		_cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CancelHandle();
			}
		});
		
		
		String removeFiles = "rm -f " + Main._FILEPATH + "/newCreation/tempAudio.wav " + Main._FILEPATH + "/newCreation/tempAudio.mp4";
		try {
			BashCommandClass.runBashProcess(removeFiles);
		} catch (IOException | InterruptedException e) {
			
		}
	}
	
	@FXML
	private void BackHandle() {
		ResetScene(); 
		
		try {
			String removeFolder = "rm -r " + Main._FILEPATH + "/newCreation";
			BashCommandClass.runBashProcess(removeFolder);
			
			changeScene(_gridPane, "/fxml/SearchScene.fxml");
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
		}
	}
	
	@FXML
	private void NextHandle() {
		ResetScene();
		
		try {
			changeScene(_gridPane, "/fxml/SelectAudioScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}
}
