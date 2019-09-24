package controllers;

import java.io.IOException;

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
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("You can't search for nothing!");

			alert.showAndWait();
			
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
			try {
				quit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		//When search finishes
		wikitBG.setOnSucceeded(succeeded -> {
			//Check for successful search
			if (wikitBG.getValue()) {
				try {
					changeScene(_gridPane, "/fxml/CreateAudioScene.fxml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				//Search failed, inform user and go back to search screen
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Could not complete search for '" + searchTerm + "'");

				alert.showAndWait();
				
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
				try {
					quit();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void quit() throws IOException {
		CreationProcess.destroy();
		changeScene(_gridPane, "/fxml/MainMenuPane.fxml");
	}
}
