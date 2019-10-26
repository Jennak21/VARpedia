package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.AudioChunk;
import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import application.WarningAlert;
import background.AudioBackgroundTask;
import background.PlayAudioBackgroundTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Controller for audio scene
 * @author Max Gurr & Jenna Kumar
 *
 */
public class CreateAudioController extends SceneChanger {
	@FXML
	private GridPane _gridPane;
	@FXML
	private GridPane _settingsGrid;
	@FXML
	private GridPane _filenameGrid;
	@FXML
	private TextArea _searchResult;
	@FXML
	private Button _resetButton;
	@FXML
	private Button _nextButton;
	@FXML
	private Button _backButton;
	@FXML
	private ChoiceBox<String> _voiceDropDown;
	@FXML
	private Button _previewButton;
	@FXML
	private Button _saveButton;
	@FXML
	private TableView<AudioChunk> _audioChunkTable;
	@FXML
	private Button _listenChunkButton;
	@FXML
	private Button _deleteChunkButton;
	@FXML
	private Button _chunkUpButton;
	@FXML
	private Button _chunkDownButton;
	@FXML
	private GridPane _confirmDeleteGrid;
	@FXML
	private Button _noDeleteButton;
	@FXML
	private Button _yesDeleteButton;
	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;
	@FXML
	private TextArea _promptText;
	
	private CreationProcess _process;
	private PlayAudioBackgroundTask _preview;
	private String _selectedText;
	private String _selectedVoice;
	private AudioChunk _selectedChunk;
	private List<AudioChunk> _chunkList;

	@FXML
	/**
	 * Run when scene loads
	 */
	public void initialize() {
		//make mouse focus on search saveButton
		Platform.runLater(()-> _saveButton.requestFocus());
		
		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		_confirmDeleteGrid.setVisible(false);
		
		//Set search text and chunk list
		_process = CreationProcess.getInstance();
		_searchResult.setText(_process.getSearchText());
		_chunkList = _process.getAudioChunks();
		
		//Get available festival voices and set in dropdown
		List<String> voices = new ArrayList<String>();
		ObservableList<String> voiceChoices = FXCollections.observableArrayList();
		try {
			String getVoices = "ls /usr/share/festival/voices/english";
			voices = BashCommandClass.getListOutput(getVoices);
			for (String s: voices) {
				voiceChoices.add(s);
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't get voices");
		}
		_voiceDropDown.setItems(voiceChoices);
		_voiceDropDown.getSelectionModel().selectFirst();
		
		//Load audio chunks into table
		loadChunkTable();
		_audioChunkTable.getSelectionModel().selectFirst();
		updateSelected();
		
		//Check if any audio files exist, if not then disable relevant buttons
		if (_process.getAudioChunks().size() == 0) {
			_nextButton.setDisable(true);
			_listenChunkButton.setDisable(true);
			_deleteChunkButton.setDisable(true);
		}
		
		
		//Make folder for audio chunks
		try {
			String audioFolder = "mkdir -p " + Main._FILEPATH + "/newCreation/audioChunks";
			BashCommandClass.runBashProcess(audioFolder);
		} catch (IOException | InterruptedException e) {}
	}
	
	/**
	 * Setup table for audio chunks, load any existing chunks
	 */
	private void loadChunkTable() {
		//Set tableview data to list of AudioChunk objects
		ObservableList<AudioChunk> data = FXCollections.observableList(_chunkList);
		_audioChunkTable.setItems(data);
        
		//Make columns
        TableColumn<AudioChunk, String> textCol = new TableColumn<>("Text");
        textCol.setCellValueFactory(new PropertyValueFactory<AudioChunk, String>("text"));
        textCol.prefWidthProperty().bind(_audioChunkTable.widthProperty().multiply(0.7));
        textCol.maxWidthProperty().bind(textCol.prefWidthProperty());
        textCol.setSortable(false);

        TableColumn<AudioChunk, String> voiceCol = new TableColumn<>("Voice");
        voiceCol.setCellValueFactory(new PropertyValueFactory<AudioChunk, String>("voice"));
        voiceCol.prefWidthProperty().bind(_audioChunkTable.widthProperty().multiply(0.3));
        voiceCol.maxWidthProperty().bind(voiceCol.prefWidthProperty());
        voiceCol.setSortable(false);
        
        //Put columns into table	
        _audioChunkTable.getColumns().setAll(textCol, voiceCol);
        _audioChunkTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	/**
	 * Update selected audio chunk
	 */
	@FXML
	private void updateSelected() {
		//Get selected chunk from table
		AudioChunk selection = (AudioChunk) _audioChunkTable.getSelectionModel().getSelectedItem();
		_selectedChunk = selection;
	}
	
	/**
	 * Listen to selected audio chunk
	 */
	@FXML
	private void listenChunkHandle() {
		if (_listenChunkButton.getText().equals("Listen") && _selectedChunk != null) {
			stopAudio();
			
			//Get file num of selected chunk
			int chunkNum = _selectedChunk.getNum();
			
			_listenChunkButton.setText("Stop");
			
			//Play chunk
			_preview = new PlayAudioBackgroundTask("" + chunkNum + Creation.AUDIO_EXTENTION, Main._FILEPATH + "/newCreation/audioChunks/");
			Thread previewThread = new Thread(_preview);
			previewThread.start();
			
			_preview.setOnSucceeded(finish -> {
				_listenChunkButton.setText("Listen");
			});
		} else {
			//Stop playback
			stopAudio();
		}
	}
	
	@FXML
	/**
	 * Delete chunk method
	 */
	private void deleteChunkHandle() {
		if (_selectedChunk != null) {
			//If there is a selected chunk, show confirm grid
			_confirmDeleteGrid.setVisible(true);
		}
	}
	
	@FXML
	/**
	 * Confirm delete
	 */
	private void yesDeleteHandle() {
		//Get file num of selected chunk
		int chunkNum = _selectedChunk.getNum();
		
		try {
			//Delete chunk
			String deleteAudio = "rm -f " + Main._FILEPATH + "/newCreation/audioChunks/" + chunkNum + Creation.AUDIO_EXTENTION;
			BashCommandClass.runBashProcess(deleteAudio);
			
			//Remove chunk from list
			_process.deleteAudioChunk(_selectedChunk);
			
			//Reload table
			loadChunkTable();
			_audioChunkTable.getSelectionModel().selectFirst();
			updateSelected();
			
			//Disable buttons if no chunks left
			if (_process.getAudioChunks().size() == 0) {
				_listenChunkButton.setDisable(true);
				_deleteChunkButton.setDisable(true);
			}
			
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't delete audio");
		}
		
		_confirmDeleteGrid.setVisible(false);
	}
	
	@FXML
	/**
	 * Cancel delete
	 */
	private void noDeleteHandle() {
		//Hide confirm grid
		_confirmDeleteGrid.setVisible(false);
	}
	
	@FXML
	/**
	 * Move selected chunk up in list
	 */
	private void chunkUpHandle() {
		if (_selectedChunk != null) {
			//Get list of chunks
			List<AudioChunk> audioChunks = _process.getAudioChunks();
			int chunkIndex = audioChunks.indexOf(_selectedChunk);
			
			//If selected chunk is not first in list
			if (chunkIndex != 0) {
				//Swap chunk with above chunk
				AudioChunk tempChunk = audioChunks.get(chunkIndex-1);
				audioChunks.set(chunkIndex-1, _selectedChunk);
				audioChunks.set(chunkIndex, tempChunk);
				
				//Refresh table
				loadChunkTable();
			}
		}
	}
	
	@FXML
	/**
	 * Move selected chunk down in list
	 */
	private void chunkDownHandle() {
		if (_selectedChunk != null) {
			//Get list of chunks
			List<AudioChunk> audioChunks = _process.getAudioChunks();
			int chunkIndex = audioChunks.indexOf(_selectedChunk);
			
			//If chunk is not last in list
			if (chunkIndex != audioChunks.size()-1) {
				//Swap chunk with below chunk
				AudioChunk tempChunk = audioChunks.get(chunkIndex+1);
				audioChunks.set(chunkIndex+1, _selectedChunk);
				audioChunks.set(chunkIndex, tempChunk);
				
				//Refresh table
				loadChunkTable();
			}
		}
	}
	
	@FXML
	/**
	 * Help button method
	 */
	private void helpHandle(ActionEvent event) {
		//Show help info
		_helpPane.setVisible(true);
		_helpText.setVisible(true);
		_helpButton.setVisible(false);
	}
	
	@FXML
	/**
	 * Exit help
	 */
	private void exitHelpHandle(ActionEvent event) {
		//Hide help info
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		_helpButton.setVisible(true);
	}
	
	@FXML
	/**
	 * When user clicks on search result text
	 */
	private void onTextClickHandle(MouseEvent event) {
		//remove prompt text
		_promptText.setVisible(false);
	}
	
	@FXML
	/**
	 * Reset search text
	 */
	private void resetHandle() {
		_searchResult.setText(_process.getSearchText());
	}
	
	@FXML
	/**
	 * Setup audio preview
	 */
	private synchronized void previewHandle() {
		//Action dependent on whether there is a preview running or not
		if (_previewButton.getText().equals("Preview Audio")) {
			stopAudio();
			
			//Create files for preview text and settings
			if (createTextFile() && createSettingsFile()) {
				_previewButton.setDisable(true);
				
				//Background task for making audio file
				AudioBackgroundTask createAudio = new AudioBackgroundTask("tempAudio", Main._FILEPATH + "/newCreation/");
				Thread audioThread = new Thread(createAudio);
				audioThread.start();
				
				//When audio finished being made
				createAudio.setOnSucceeded(finish -> {
					//If successfully made audio, play it
					if (createAudio.getValue()) {
						previewAudio();
					} else {
						new ErrorAlert("Couldn't play audio");
						_previewButton.setDisable(false);
					}
				});
			}
		} else {
			//Stop playback
			stopAudio();
		}
	}
	
	/**
	 * Run background task for audio preview
	 */
	private void previewAudio() {
		//GUI changes for audio playing
		_previewButton.setDisable(false);
		_previewButton.setText("Stop preview");
		
		//Play audio in background task
		_preview = new PlayAudioBackgroundTask("tempAudio" + Creation.AUDIO_EXTENTION, Main._FILEPATH + "/newCreation/");
		Thread previewThread = new Thread(_preview);
		previewThread.start();
		
		//When audio is finished
		_preview.setOnSucceeded(finish -> {
			//Update GUI
			_previewButton.setText("Preview Audio");
			try {		
				//Remove temp audio file
				String removeFile = "rm -f " + Main._FILEPATH + "/newCreation/tempAudio" + Creation.AUDIO_EXTENTION;
				BashCommandClass.runBashProcess(removeFile);
			} catch (IOException | InterruptedException e) {
				new ErrorAlert("Something went wrong");
			}
		});
	}
	
	@FXML
	/**
	 * Save selected text
	 */
	private void saveHandle() {		
		if (_saveButton.getText().equals("Save Audio")) {
			if (createTextFile() && createSettingsFile()) {
				_saveButton.setText("Saving...");
				
				//Figure out audio chunk num
				int chunkNum = _process.getNumChunks() + 1;
				
				//Background task for making audio file
				AudioBackgroundTask createAudio = new AudioBackgroundTask("" + chunkNum, Main._FILEPATH + "/newCreation/audioChunks/");
				Thread audioThread = new Thread(createAudio);
				audioThread.start();
				
				//When new audio finished being saved
				createAudio.setOnSucceeded(finish -> {
					//If successfully saved audio
					if (createAudio.getValue()) {
						new InformationAlert("Added new audio");
						
						//Add new audio chunk
						AudioChunk newAudioChunk = new AudioChunk(chunkNum, _selectedText, _selectedVoice);
						_process.addAudioChunk(newAudioChunk);
											
						//Reset GUI
						_nextButton.setDisable(false);
						_listenChunkButton.setDisable(false);
						_deleteChunkButton.setDisable(false);
						
						loadChunkTable();
						_audioChunkTable.getSelectionModel().selectFirst();
						updateSelected();
						
					} else {
						new ErrorAlert("Couldn't save audio");
					}
					
					_saveButton.setText("Save Audio");
				});
			}
		}
	}
	
	/**
	 * Create settings file for audio
	 * @return boolean - Success status of file creation
	 */
	private boolean createSettingsFile() {
		//Get selected voice
		_selectedVoice = (String)_voiceDropDown.getSelectionModel().getSelectedItem();
		
		//Check for valid voice
		if (_selectedVoice != null && !_selectedVoice.isEmpty()) {
			try {
				//Create text file
				String voiceSetting = "(voice_" + _selectedVoice + ")";
				String settingsFile = "echo \"" + voiceSetting + "\" > " + Main._FILEPATH + "/newCreation/settings.scm";
				BashCommandClass.runBashProcess(settingsFile);
				
				return true;
			} catch (IOException | InterruptedException e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Create text file to read audio from
	 * @return boolean - Success of file creation
	 */
	private boolean createTextFile() {
		//Get selected text
		_selectedText = _searchResult.getSelectedText();
		
		//Variable for valid text selected
		boolean valid = true;
		
		//Check for non-empty selection
		if (_selectedText == null || _selectedText.isEmpty()) {
			valid = false;
		}
		
		//Count number of words in selection
		String[] words = _selectedText.split("\\s+");
		int length = words.length;
		
		//Check for too long selection
		if (length > 40) {
			valid = false;
		}
		
		//If valid selection, make text file
		if (valid) {
			try {
				String textFile = "echo \"" + _selectedText + "\" > " + Main._FILEPATH + "/newCreation/text.txt";
				BashCommandClass.runBashProcess(textFile);
				
				return true;
			} catch (IOException | InterruptedException e) {
				return false;
			}
		} else {
			new WarningAlert("Please select between 1-40 words");
			return false;
		}
	}	
	
	@FXML
	/**
	 * Go back to search scene
	 */
	private void backHandle() {		
		try {
			//Stop any audio playback
			stopAudio();
			
			//Delete newCreation folder to reset files
			String removeFolder = "rm -r " + Main._FILEPATH + "/newCreation";
			BashCommandClass.runBashProcess(removeFolder);
			
			CreationProcess.destroy();
			
			changeScene(_gridPane, "/fxml/SearchScene.fxml");
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
		}
	}
	
	@FXML
	/**
	 * Go to select images scene
	 */
	private void nextHandle() {
		try {
			String removeTempaudio = "rm -f " + Main._FILEPATH + "/newCreation/tempAudio" + Creation.AUDIO_EXTENTION;
			BashCommandClass.runBashProcess(removeTempaudio);
			
			stopAudio();
			
			changeScene(_gridPane, "/fxml/SelectImageScene.fxml");
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't change scene");
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop audio playback
	 */
	private void stopAudio() {
		if (_preview != null) {
			_preview.stopProcess();
		}
	}
}
