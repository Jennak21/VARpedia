package controllers;

import java.io.IOException;

import application.BashCommandClass;
import application.ErrorAlert;
import application.WarningAlert;
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
import javafx.scene.layout.GridPane;

public class CreateAudioController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
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
	private Label _titleLabel;
	
	CreationProcess process;
	PreviewBackgroundTask preview;
	
	@FXML
	public void initialize() {
		process = CreationProcess.getInstance();
		_textArea.setText(process.getSearchText());
		

		ObservableList<String> voiceChoices = FXCollections.observableArrayList("voice_rab_diphone"); 
		_voiceDropDown.setItems(voiceChoices);
		
		_voiceDropDown.getSelectionModel().selectFirst();
	}
	
	@FXML
	private void ResetHandler() {
		_textArea.setText(process.getSearchText());
	}
	
	@FXML
	private void PreviewHandler() {
		if (preview == null || preview.isDone()) {
			String selectedText = _textArea.getSelectedText();
			
			if (selectedText.isEmpty()) {
				new WarningAlert("Please select text to preview");
				return;
			}
			
			preview = new PreviewBackgroundTask(selectedText);
			Thread previewThread = new Thread(preview);
			previewThread.start();
		
			preview.setOnRunning(running -> {
				_backButton.setDisable(true);
				_nextButton.setDisable(true);
			});
			
			preview.setOnSucceeded(finish -> {
				_backButton.setDisable(false);
				_nextButton.setDisable(false);
			});
		} else {
			new WarningAlert("Please wait until current preview is done");
		}
	}
	
	@FXML
	private void SaveHandler() {
		String voice = (String)_voiceDropDown.getSelectionModel().getSelectedItem();
		System.out.println(voice);
	}
	
	@FXML
	private void BackHandler() {
		try {
			changeScene(_gridPane, "/fxml/SearchScene.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void NextHandler() {
		try {
			changeScene(_gridPane, "/fxml/SelectAudioScene");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}
}
