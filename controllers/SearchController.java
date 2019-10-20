package controllers;

import java.io.IOException;

import application.ErrorAlert;
import application.InformationAlert;
import application.WarningAlert;
import background.WikitBackgroundTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class SearchController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
	@FXML
	private Label _titleLabel;
	@FXML
	private TextField _searchField;
	@FXML
	private Button _searchButton;
	@FXML
	private Button _backButton;

	private String _searchTerm;

	@FXML
	private ProgressBar _progressBar;

	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;

	@FXML
	private void initialize() {

		_progressBar.setVisible(false);

		_searchButton.setDisable(true);

		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);


	}

	@FXML
	private void backHandle(ActionEvent event) throws IOException {
		quit();
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
	private void onTypeHandler(KeyEvent event) {
		//check if field properties are empty or not, set create button to  be disabled or not accordingly
		_searchTerm = _searchField.getText().trim();
		boolean isDisabled = _searchTerm.isEmpty();
		_searchButton.setDisable(isDisabled);

		if ((event.getCode() == KeyCode.ENTER) && !_searchTerm.isEmpty() && !_searchTerm.trim().isEmpty()) {
			search();
		}
	}



	@FXML
	private void searchHandle() {
		search();
	}

	public void search () {
		_searchButton.setDisable(true);

		//		String searchTerm = _searchField.getText();

		//Check for non-empty search term
		//		if (searchTerm.isEmpty()) {
		//			new WarningAlert("You can't search for nothing");
		//			reset();
		//			
		//			return; 
		//		} 

		//Run search on background thread
		WikitBackgroundTask wikitBG = new WikitBackgroundTask(_searchTerm);
		Thread wikitThread = new Thread(wikitBG);
		wikitThread.start();

		//Update user that search is happening
		wikitBG.setOnRunning(running -> {
			//			_searchingLabel.setVisible(true);
			_progressBar.setProgress(-1.0);
			_progressBar.setVisible(true);

			//Cancel search if user wants to quit
			_backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					wikitBG.cancel();	
				}
			});
		});

		//If user has cancelled, go back to main screen
		wikitBG.setOnCancelled(cancel -> {
			quit();
		});

		//When search finishes
		wikitBG.setOnSucceeded(succeeded -> {
			//Check for successful search
			if (wikitBG.getValue()) {
				try {
					changeScene(_gridPane, "/fxml/CreateAudioScene.fxml");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				//Search failed, get fail message
				String returnMessage = wikitBG.getMessage();
				String errorMessage;
				if (returnMessage.equals("Empty")) {
					errorMessage = "Could not retrieve information for '" + _searchTerm + "'";
				} else if (returnMessage.equals("Ambiguous")) {
					errorMessage = "Please enter a more specific term";
				} else if (returnMessage.equals("No results")) {
					errorMessage = "No results found for '" + _searchTerm + "'";
				} else {
					errorMessage = "Unknown error";
				}
				
				new ErrorAlert("Error: " + errorMessage);
				reset();
			}
		});

	}

	private void reset() {
		_searchField.clear();
		_searchButton.setDisable(true);
		_progressBar.setVisible(false);

		_backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				quit();
			}
		});
	}

	private void quit() {
		CreationProcess.destroy();
		try {
			changeScene(_gridPane, "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scene");
		}
	}
}
