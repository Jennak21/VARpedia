package controllers;

import java.io.IOException;

import application.ErrorAlert;
import application.WarningAlert;
import background.WikitBackgroundTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
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
	@FXML
	private Label _searchingLabel;
	
	@FXML
	private void BackHandle(ActionEvent event) throws IOException {
		quit();
	}
	
	@FXML
	private void SearchHandle() {
		_searchButton.setDisable(true);
		
		String searchTerm = _searchField.getText();
		
		//Check for non-empty search term
		if (searchTerm.isEmpty()) {
			new WarningAlert("You can't search for nothing");
			reset();
			
			return; 
		} 
				
		//Run search on background thread
		WikitBackgroundTask wikitBG = new WikitBackgroundTask(searchTerm);
		Thread wikitThread = new Thread(wikitBG);
		wikitThread.start();
		
		//Update user that search is happening
		wikitBG.setOnRunning(running -> {
			_searchingLabel.setVisible(true);
			
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
				//Search failed, inform user and go back to search screen
				new ErrorAlert("Could not complete search for '" + searchTerm + "'");
				reset();
			}
		});
	}
	
	private void reset() {
		_searchField.clear();
		_searchButton.setDisable(false);
		_searchingLabel.setVisible(false);
		
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
