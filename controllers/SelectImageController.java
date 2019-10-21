package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import background.DownloadImageBackgroundTask;
import background.PlayAudioBackgroundTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class SelectImageController extends SceneChanger implements Initializable{
	private CreationProcess _creationProcess;
	private String _allImagesFilePath = Main._FILEPATH +"/newCreation/allImages";
	
	@FXML
	private ProgressIndicator _progressIndicator;

	@FXML
	private Text _imageText;

	@FXML
	private Text _musicText;

	@FXML
	private ChoiceBox<String> _musicChoiceBox;

	@FXML
	private Button _nextButton;

	@FXML
	private Button _backButton;

	@FXML
	private TableView<ImageTable> _imageTable;

	@FXML 
	private TableColumn<ImageTable, ImageView> _imageCol;

	@FXML 
	private TableColumn<ImageTable, CheckBox> _selectCol;
	
	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;
	
	@FXML
	private Button _musicButton;
	
	private PlayAudioBackgroundTask _musicTask;
	

	private ObservableList<ImageTable> _imageList = FXCollections.observableArrayList();

	private DownloadImageBackgroundTask _downloadImage;
	




	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {


		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);

		_nextButton.setDisable(true);

		_creationProcess = CreationProcess.getInstance();

		_creationProcess.clearSelectedImages();

		//create thread to downloads images
		_downloadImage = new DownloadImageBackgroundTask();
		Thread downloadImageThread = new Thread(_downloadImage);
		downloadImageThread.start();

		_downloadImage.setOnRunning(running -> {
			//Change text to inform the user and have the progressIndicator running
			_progressIndicator.setProgress(-1.0);
			_imageText.setText("Fetching images for your creation"); 	

		});

		//once images have been downloaded, load the images to the table and change the label
		_downloadImage.setOnSucceeded(finish -> {
			_imageText.setText("Select images for your creation"); 	
			loadDataToTable();
			_progressIndicator.setVisible(false);
		});

		
		//Get available background music and set in dropdown
		List<String> music = new ArrayList<String>();
		ObservableList<String> musicChoices = FXCollections.observableArrayList();
		musicChoices.add("None");
		
		try {
			String getMusic = "basename -s .mp3 -a $(ls " + Main._RESOURCEPATH + "/*.mp3)";
			music = BashCommandClass.getListOutput(getMusic);
			for (String s: music) {
				musicChoices.add(s);
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't get voices");
		}
		
		_musicChoiceBox.setItems(musicChoices);
		_musicChoiceBox.getSelectionModel().selectFirst();
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



	public void loadDataToTable () {
		File dir = new File(_allImagesFilePath);

		if (dir.isDirectory()) { // make sure it's a directory
			for (final File file : dir.listFiles()){

				CheckBox checkBox = new CheckBox();
				checkBox.setPrefSize(150, 250);
				
				
				//add event handler to checkbox where if the checkbox is selected, the next button is enabled
				EventHandler<ActionEvent> checkedHandler = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (event.getSource() instanceof CheckBox) {
							CheckBox chk = (CheckBox) event.getSource();
							if (chk.isSelected()) {
								//make the mouse focus on the next button
								_nextButton.setDisable(false);
								_nextButton.requestFocus();
								
							//if checkbox is not selected, then if all checkboxes are unselected disable next button	
							} else {
								for (ImageTable image : _imageList) {
									if (image.getCheckBox().isSelected()) {
										_nextButton.setDisable(false);

										break;		
									}
									_nextButton.setDisable(true); 
								}
							}
						}
					}
				};

				checkBox.setOnAction(checkedHandler);

				//create image and then image view
				Image image = new Image(file.toURI().toString(), 300, 250, false, false);
				ImageView iv = new ImageView(image);

				//create new image object which will store the image, checkbox and filepath and then
				//add it to the observable list
				_imageList.add(new ImageTable(iv, checkBox, file.getAbsolutePath()));
			}
		}

		//set up columns in both tables
		_imageCol.setCellValueFactory(new PropertyValueFactory<ImageTable, ImageView>("Image"));
		_selectCol.setCellValueFactory(new PropertyValueFactory<ImageTable, CheckBox>("CheckBox"));
		_imageTable.setItems(_imageList);

		_imageTable.setSelectionModel(null);
	}

	
	@FXML
	private void musicHandle() {
		if (_musicTask != null && _musicTask.isAlive()) {
			stopMusic();
		} else {
			String bgMusic = _musicChoiceBox.getSelectionModel().getSelectedItem();
			if (!bgMusic.equals("None")) {	
				_musicTask = new PlayAudioBackgroundTask(bgMusic + ".wav", Main._RESOURCEPATH + "/");
				
				Thread musicThread = new Thread(_musicTask);
				musicThread.start();
			}
		}
	}
	
	private void stopMusic() {
		if (_musicTask != null) {
			_musicTask.stopProcess();
		}
	}
	

	@FXML
	public void onNextHandler(ActionEvent event) {
		stopMusic();

		ArrayList<String> imageList = new ArrayList<String>();

		//Iterate through images in the observable list and if it is selected, add the filepath of the image to a list
		for (ImageTable image : _imageList) {
			if (image.getCheckBox().isSelected()) {
				imageList.add(image.getFilePath());	
			}	
		}

		//store the list of image filepaths
		_creationProcess.setImageList(imageList);

		//store selected music
		String bgMusic = _musicChoiceBox.getSelectionModel().getSelectedItem();
		_creationProcess.setBGMusic(bgMusic);
		
		try {
			changeScene((Node)event.getSource(), "/fxml/FileNameScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}

	}

	@FXML
	public void onBackHandler(ActionEvent event) {
		stopMusic();
		
		try {
			changeScene((Node)event.getSource(), "/fxml/CreateAudioScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}

	}

}
