package controllers;



import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;

import application.BashCommandClass;
import application.Creation;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class SelectAudioController extends SceneChanger implements Initializable{

	@FXML
	private TableView<AudioTable> _availableAudioTable;

	@FXML 
	private TableColumn<AudioTable, String> _availableAudioCol;

	@FXML
	private TableView<AudioTable> _selectedAudioTable;

	@FXML 
	private TableColumn<AudioTable, String> _selectedAudioCol;

	@FXML
	private Button _selectButton;

	@FXML
	private Button _unselectButton;

	@FXML
	private Button _nextButton;

	@FXML
	private Button _backButton;

	@FXML
	private Spinner<Integer> _numImagesSpinner;

	private ObservableList<AudioTable> _availablelist = FXCollections.observableArrayList();
	private ObservableList<AudioTable> _selectedlist = FXCollections.observableArrayList();

	private AudioTable _selectedAvailableAudio;
	private AudioTable _selectedSelectedAudio;
	
	private String _filePath = Main._FILEPATH +"/newCreation/";



	private CreationProcess _creationProcess;

	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
		_numImagesSpinner.setValueFactory(spinnerValues);

		_unselectButton.setDisable(true);
		_selectButton.setDisable(true);
		_nextButton.setDisable(true);
		
		_creationProcess = CreationProcess.getInstance();
		loadData();
		
	}

	public void loadData() {
		List<String> audioNames = getAudioNames();	

		for (String audioName : audioNames) {

			_availablelist.add(new AudioTable(audioName));

		}

		//_creationList.getItems().addAll( _observeList);


		_availableAudioCol.setCellValueFactory(new PropertyValueFactory<AudioTable, String>("AudioName"));
		_availableAudioTable.setItems(_availablelist);

		_selectedAudioCol.setCellValueFactory(new PropertyValueFactory<AudioTable, String>("AudioName"));
		_selectedAudioTable.setItems(_selectedlist);




		_selectedAudioTable.setRowFactory(tv -> {
			TableRow<AudioTable> row = new TableRow<>();

			row.setOnDragDetected(event -> {
				if (! row.isEmpty()) {
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(SERIALIZED_MIME_TYPE, index);
					db.setContent(cc);
					event.consume();
				}
			});

			row.setOnDragOver(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE)) {
					if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						event.consume();
					}
				}
			});

			row.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE)) {
					int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
					AudioTable draggedAudio = _selectedAudioTable.getItems().remove(draggedIndex);

					int dropIndex ; 

					if (row.isEmpty()) {
						dropIndex = _selectedAudioTable.getItems().size() ;
					} else {
						dropIndex = row.getIndex();
					}

					_selectedAudioTable.getItems().add(dropIndex, draggedAudio);

					event.setDropCompleted(true);
					_selectedAudioTable.getSelectionModel().select(dropIndex);
					event.consume();
				}
			});

			return row ;
		});
	} 

	@FXML
	private void onBackHandler(ActionEvent event) {

	}

	@FXML
	private void onNextHandler(ActionEvent event) {

		int numImages = _numImagesSpinner.getValue();
		
		_creationProcess.setNumImages(numImages);
		
		ArrayList<String> audioSelectedList = new ArrayList<String>();

		for (AudioTable audio : _selectedlist) {
			audioSelectedList.add(audio.getAudioName());
		}
		
		_creationProcess.setAudioFiles(audioSelectedList);
		
		
		try {
			changeScene((Node)event.getSource(), "/fxml/FileNameScene.fxml") ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void onSelectButtonHandler(ActionEvent event) {
		_selectedlist.add(_selectedAvailableAudio);			
		_availablelist.remove(_selectedAvailableAudio);	

		if (_availablelist.indexOf(_selectedAvailableAudio)== (_availablelist.size() -1)) {
			_selectButton.setDisable(true);
		} else {
			_selectedAvailableAudio = _availablelist.get(0);
		}

		_nextButton.setDisable(false);
	}

	@FXML
	private void onUnselectButtonHandler(ActionEvent event) { 
		_availablelist.add(_selectedSelectedAudio);	
		_selectedlist.remove(_selectedSelectedAudio);			

		if (_selectedlist.indexOf(_selectedSelectedAudio)== (_selectedlist.size() -1)) {
			_unselectButton.setDisable(true);
			_nextButton.setDisable(true);
		} else {
			_selectedSelectedAudio = _selectedlist.get(0);
		}
	}

	@FXML
	private void onAvailableClickHandler(MouseEvent event) {
		_unselectButton.setDisable(true);

		_selectedAvailableAudio = _availableAudioTable.getSelectionModel().getSelectedItem();

		if (_selectedAvailableAudio  == null ) {
			_selectButton.setDisable(true);

		}else {
			_selectButton.setDisable(false);		 
		}

	}

	@FXML
	private void onSelectedClickHandler(MouseEvent event) {
		_selectButton.setDisable(true);
		_selectedSelectedAudio = _selectedAudioTable.getSelectionModel().getSelectedItem();

		if (_selectedSelectedAudio  == null ) {
			_unselectButton.setDisable(true);

		}else {
			_unselectButton.setDisable(false);		 
		}
	}

	public List<String> getAudioNames() {
		//create a text file that stores file names
		String getAudioFileNameCommand = "ls -1a " + _filePath +"*" + Creation.AUDIO_EXTENTION +" | sed -r \"s/.+\\/(.+)\\..+/\\1/\"";
		try {
			return BashCommandClass.getListOutput(getAudioFileNameCommand);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return empty list
		 List<String> list = new ArrayList<String>();
		 return list;
	}


}
