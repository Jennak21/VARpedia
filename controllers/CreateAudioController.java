package controllers;

import java.io.IOException;

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
	
	@FXML
	public void initialize() {
		_process = CreationProcess.getInstance();
		_textArea.setText(_process.getSearchText());
		

		ObservableList<String> voiceChoices = FXCollections.observableArrayList("voice_rab_diphone"); 
		_voiceDropDown.setItems(voiceChoices);
		
		_voiceDropDown.getSelectionModel().selectFirst();
		
		try {
			String checkAudio = "ls " + Main._FILEPATH + "/newCreation/*" + Creation.AUDIO_EXTENTION;
			int exitVal = BashCommandClass.runBashProcess(checkAudio);
			
			if (exitVal != 0) {
				_nextButton.setDisable(true);
			}
		} catch (IOException | InterruptedException e) {
			
		}
		
		
		ResetScene();
	}
	
	@FXML
	private void ResetHandle() {
		_textArea.setText(_process.getSearchText());
	}
	
	@FXML
	private void PreviewHandle() {
		if (_preview == null || _preview.isDone()) {
			String selectedText = _textArea.getSelectedText();
			
			if (selectedText.isEmpty()) {
				new WarningAlert("Please select text to preview");
				return;
			}
			
			_preview = new PreviewBackgroundTask(selectedText);
			Thread previewThread = new Thread(_preview);
			previewThread.start();
		
			_preview.setOnRunning(running -> {
				_backButton.setDisable(true);
				_nextButton.setDisable(true);
			});
			
			_preview.setOnSucceeded(finish -> {
				_backButton.setDisable(false);
				_nextButton.setDisable(false);
			});
		} else {
			new WarningAlert("Please wait until current preview is done");
		}
	}
	
	@FXML
	private void SaveHandle() {
		_selectedText = _textArea.getSelectedText();
		
		if (_selectedText.isEmpty()) {
			new WarningAlert("Please select text to preview");
			return;
		}
		
		_selectedVoice = (String)_voiceDropDown.getSelectionModel().getSelectedItem();
		
		FilenameGrid();
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
	
	@FXML
	private void YesHandle() {
		AudioBackgroundTask audioTask = new AudioBackgroundTask(_selectedText, _selectedVoice, _filename);
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
		
		_cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CancelHandle();
			}
		});
	}
	
	@FXML
	private void BackHandle() {
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
		try {
			changeScene(_gridPane, "/fxml/SelectAudioScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}
}
