package controllers;

import java.io.IOException;

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
	
	
	@FXML
	public void initialize() {
		CreationProcess process = CreationProcess.getInstance();
		System.out.println(process.getSearchTerm());
	}
	
	@FXML
	private void ResetHandler() {
		
	}
	
	@FXML
	private void PreviewHandler() {
		
	}
	
	@FXML
	private void SaveHandler() {
		
	}
	
	@FXML
	private void BackHandler() throws IOException {
		changeScene(_gridPane, "/fxml/SearchScene.fxml");
	}
	
	@FXML
	private void NextHandler() {
		
	}
}
