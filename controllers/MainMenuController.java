package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController extends SceneChanger {
//	@FXML
//	private GridPane _mainMenuGridPane;
	@FXML
	private TableView _creationTable;
	@FXML
	private Button _playButton;
	@FXML
	private Button _deleteButton;
	@FXML
	private Button _createButton;
	@FXML
	private Button _quizButton;	
	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;
	
	
	@FXML
	private void initialize() {
		cleanUp();
		
		//focus on table
		Platform.runLater(()->_creationTable.requestFocus());
		
		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		
		List<Creation> creationList = Main.getCreationList();
		
		if (creationList.size() < 1) {
			_playButton.setDisable(true);
			_deleteButton.setDisable(true);
			_quizButton.setDisable(true);
		} else {
			_playButton.setDisable(false);
			_deleteButton.setDisable(false);
			_quizButton.setDisable(false);
		}
		
		setupTable(creationList);
	}
	
	private void setupTable(List<Creation> creationList) {
		//Set tableview data to list of creation objects
		ObservableList<Creation> data = FXCollections.observableList(creationList);
		
		data = FXCollections.observableList(creationList);
		_creationTable.setItems(data);
        
        TableColumn<Creation, String> nameCol = new TableColumn<>("Filename");
        nameCol.setCellValueFactory(new PropertyValueFactory<Creation, String>("filename"));
        TableColumn<Creation, String> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<Creation, String>("length"));
        TableColumn<Creation, String> accCol = new TableColumn<>("Learning");
        accCol.setCellValueFactory(new PropertyValueFactory<Creation, String>("testAcc"));
        
        _creationTable.getColumns().setAll(nameCol, lengthCol, accCol);
        _creationTable.setPrefWidth(450);
        _creationTable.setPrefHeight(300);
        _creationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        _creationTable.getSelectionModel().selectFirst();
	}
	
	@FXML
	private void playHandle(ActionEvent event) {
		_playButton.setText("Playing");
		CreationStore process = CreationStore.getInstance();
		
		Creation selected = (Creation) _creationTable.getSelectionModel().getSelectedItem();
		process.setCreation(selected);
		
		try {
			changeScene((Node)event.getSource(), "/fxml/MediaScreenPane.fxml");
		} catch (IOException e) {
			e.printStackTrace();
//			new ErrorAlert("Couldn't change scenes");
		}
	}
	
	@FXML
	private void deleteHandle(ActionEvent event) {
		
		CreationStore process = CreationStore.getInstance();
		
		//store filename of file being deleted
		
		Creation selected = (Creation) _creationTable.getSelectionModel().getSelectedItem();
		process.setCreation(selected);
		
		try {
			changeScene((Node)event.getSource(), "/fxml/DeleteScene.fxml");
		} catch (IOException e) {
			e.printStackTrace();
//			new ErrorAlert("Couldn't change scenes");
		}
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
	private void createHandle(ActionEvent event) {
		try {
			changeScene((Node)event.getSource(), "/fxml/SearchScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}
	}
	
	
	private void cleanUp() {
		try {
			String removeCommand = "rm -r " + Main._FILEPATH + "/newCreation";
			BashCommandClass.runBashProcess(removeCommand);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete files");
		}
	}
	
	@FXML
	private void quizHandle(ActionEvent event) {
		try {
			changeScene((Node)event.getSource(), "/fxml/QuizScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't load quiz");
		}
	}
}
