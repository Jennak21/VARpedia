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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainMenuController {
	@FXML
	private GridPane mainMenuGridPane;
	@FXML
	private TableView creationTable;
	@FXML
	private Button playButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button createButton;
	
	
	@FXML
	public void initialize() {
		//Storage list
		List<Creation> creationList = new ArrayList<Creation>();
		
		try {
			//Count number of creations
			String countFiles = "ls -1 " + Main.FILEPATH + "/*" + Creation.getExtention() + " | wc -l";
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
				String getCreations = "basename -s " + Creation.getExtention() + " " + Main.FILEPATH + "/*";
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
					
					String getCreationLength = "ffprobe -i " + Main.FILEPATH + "/" + s + Creation.getExtention() + " -show_entries format=duration -v quiet -of csv=\"p=0\"";
					
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
				playButton.setDisable(true);
				deleteButton.setDisable(true);
			} else {
				playButton.setDisable(false);
				deleteButton.setDisable(false);
			}
			
			
			//Set tableview data to list of creation objects
			ObservableList<Creation> data = FXCollections.observableList(creationList);
			
			
			data = FXCollections.observableList(creationList);
			creationTable.setItems(data);
	        
	        TableColumn<Creation, String> nameCol = new TableColumn<>("Name");
	        nameCol.setCellValueFactory(new PropertyValueFactory<Creation, String>("name"));
	        TableColumn<Creation, Double> lengthCol = new TableColumn<>("Length");
	        lengthCol.setCellValueFactory(new PropertyValueFactory<Creation, Double>("length"));
	        
	        creationTable.getColumns().setAll(nameCol, lengthCol);
	        creationTable.setPrefWidth(450);
	        creationTable.setPrefHeight(300);
	        creationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	        creationTable.getSelectionModel().selectFirst();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void PlayHandle() throws IOException {
		Stage stage = (Stage) mainMenuGridPane.getScene().getWindow();
		Parent layout = FXMLLoader.load(getClass().getResource("/fxml/MediaScreenPane.fxml"));
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	private void DeleteHandle() {
		
	}
	
	@FXML
	private void CreateHandle() {
		
	}
	
	
	private void cleanUp() {
		try {
			List<String> filesToRemove = new ArrayList<String>();
			filesToRemove.add(Main.FILEPATH + "/newCreation");
			
			ProcessBuilder builder;
			Process process;
			
			
			String removeCommand = "rm -r " + Main.FILEPATH + "/newCreation";
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
