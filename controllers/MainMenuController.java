package controllers;

import java.io.IOException;
import java.util.List;

import application.Main;
import customUI.ErrorAlert;
import application.BashCommandClass;
import application.Creation;
import application.CreationStore;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 * Controller for main menu
 * @author Max Gurr & Jenna Kumar
 *
 */
public class MainMenuController extends SceneChanger {
	@FXML
	private TableView<Creation> _creationTable;
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
	/**
	 * Run when scene loads
	 */
	private void initialize() {
		cleanUp();
		
		//focus on table
		Platform.runLater(()->_creationTable.requestFocus());
		
		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		
		//Get list of available creations
		List<Creation> creationList = Main.getCreationList();
		
		//Check how many creations available, if none - disable relevant controls
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
	
	/**
	 * Load data into creation table
	 * @param creationList - List of available creations
	 */
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
	/**
	 * Go to mediaplay scene
	 */
	private void playHandle(ActionEvent event) {
		//Creation creation store
		CreationStore process = CreationStore.getInstance();
		//Set creation
		Creation selected = (Creation) _creationTable.getSelectionModel().getSelectedItem();
		process.setCreation(selected);
		
		//Change to mediaplayer
		try {
			changeScene((Node)event.getSource(), "/fxml/MediaScreenPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't load video");
		}
	}
	
	@FXML
	/**
	 * Go to delete scene
	 */
	private void deleteHandle(ActionEvent event) {
		//Get process instance
		CreationStore process = CreationStore.getInstance();
		//Store filename of file being deleted
		Creation selected = (Creation) _creationTable.getSelectionModel().getSelectedItem();
		process.setCreation(selected);
		
		//Change to delete scene
		try {
			changeScene((Node)event.getSource(), "/fxml/DeleteScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}
	}
	
	@FXML
	/**
	 * Help button method
	 */
	private void helpHandle(ActionEvent event) {
		_helpPane.setVisible(true);
		_helpText.setVisible(true);
		_helpButton.setVisible(false);
	}
	
	@FXML
	/**
	 * Exit help
	 */
	private void exitHelpHandle(ActionEvent event) {
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		_helpButton.setVisible(true);

	}
	
	@FXML
	/**
	 * Go to search scene
	 */
	private void createHandle(ActionEvent event) {
		//Load searching scene
		try {
			changeScene((Node)event.getSource(), "/fxml/SearchScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}
	}
	
	/**
	 * Cleanup files for new creations
	 */
	private void cleanUp() {
		//Delete 'newCreation' folder
		try {
			String removeCommand = "rm -r " + Main._FILEPATH + "/newCreation";
			BashCommandClass.runBashProcess(removeCommand);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete files");
		}
	}
	
	@FXML
	/**
	 * Go to quiz scene
	 */
	private void quizHandle(ActionEvent event) {
		//Load quiz scene
		try {
			changeScene((Node)event.getSource(), "/fxml/QuizScene.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't load quiz");
		}
	}
}
