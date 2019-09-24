package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.Creation;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainMenuController extends SceneChanger {
	@FXML
	private GridPane _mainMenuGridPane;
	@FXML
	private TableView _creationTable;
	@FXML
	private Button _playButton;
	@FXML
	private Button _deleteButton;
	@FXML
	private Button _createButton;
	
	
	@FXML
	private void initialize() {
		//Storage list
		List<Creation> creationList = new ArrayList<Creation>();
		
		try {
			//Count number of creations
			String countFiles = "ls -1 " + Main._FILEPATH + "/*" + Creation.getExtention() + " | wc -l";
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", countFiles);
			Process process = builder.start();
			
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String numFilesOutput = stdoutBuffered.readLine();
			int numFiles = Integer.parseInt(numFilesOutput);
			
			//Do stuff stuff based on how many files were found
			if (numFiles != 0) {
				//Get names of creations
				String getCreations = "basename -s " + Creation.getExtention() + " " + Main._FILEPATH + "/*";
				builder = new ProcessBuilder("bash", "-c", getCreations);
				
				process = builder.start();
				
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Store all creation names
				List<String> nameList = new ArrayList<String>();
				stdout = process.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null ) {
					nameList.add(line);
				}
				
				//For all stored creation names, get the lengths of the creation, and make a new creation object
				for (String s : nameList) {
					
					String getCreationLength = "ffprobe -i " + Main._FILEPATH + "/" + s + Creation.getExtention() + " -show_entries format=duration -v quiet -of csv=\"p=0\"";
					
					builder = new ProcessBuilder("bash", "-c", getCreationLength);
					process = builder.start();
					
					process.waitFor();
										
					stdout = process.getInputStream();
					stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
					String length = stdoutBuffered.readLine();
					double numberLength = Double.parseDouble(length);		
					String roundedLength = String.format("%.2f", numberLength);
					
					creationList.add(new Creation(s, roundedLength));
				}
		
			}		
			
			
			if (creationList.size() < 1) {
				_playButton.setDisable(true);
				_deleteButton.setDisable(true);
			} else {
				_playButton.setDisable(false);
				_deleteButton.setDisable(false);
			}
			
			
			//Set tableview data to list of creation objects
			ObservableList<Creation> data = FXCollections.observableList(creationList);
			
			
			data = FXCollections.observableList(creationList);
			_creationTable.setItems(data);
	        
	        TableColumn<Creation, String> nameCol = new TableColumn<>("Name");
	        nameCol.setCellValueFactory(new PropertyValueFactory<Creation, String>("name"));
	        TableColumn<Creation, Double> lengthCol = new TableColumn<>("Length");
	        lengthCol.setCellValueFactory(new PropertyValueFactory<Creation, Double>("length"));
	        
	        _creationTable.getColumns().setAll(nameCol, lengthCol);
	        _creationTable.setPrefWidth(450);
	        _creationTable.setPrefHeight(300);
	        _creationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	        _creationTable.getSelectionModel().selectFirst();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void PlayHandle(ActionEvent event) throws IOException {
		MediaProcess process = MediaProcess.getInstance();
		
		Creation selected = (Creation) _creationTable.getSelectionModel().getSelectedItem();
		process.setCreation(selected);
		
		changeScene((Node)event.getSource(), "/fxml/MediaScreenPane.fxml");
	}
	
	@FXML
	private void DeleteHandle() {
		
	}
	
	@FXML
	private void CreateHandle(ActionEvent event) throws IOException {
		changeScene((Node)event.getSource(), "/fxml/SearchScene.fxml");
	}
	
	
	private void cleanUp() {
		try {
			List<String> filesToRemove = new ArrayList<String>();
			filesToRemove.add(Main._FILEPATH + "/newCreation");
			
			ProcessBuilder builder;
			Process process;
			
			
			String removeCommand = "rm -r " + Main._FILEPATH + "/newCreation";
			builder = new ProcessBuilder("bash", "-c", removeCommand);
		
			process = builder.start();	
			
			process.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
