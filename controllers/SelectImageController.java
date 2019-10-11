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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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



		_downloadImage = new DownloadImageBackgroundTask();
		Thread downloadImageThread = new Thread(_downloadImage);
		downloadImageThread.start();

		_downloadImage.setOnRunning(running -> {
			//make label visible which will show progress updates
			_progressIndicator.setProgress(-1.0);
			_imageText.setText("Fetching images for your creation"); 	

		});


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
				//checkBox.addEventHandler(Event<ActionEvent>, eventHandler);

				EventHandler<ActionEvent> checkedHandler = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (event.getSource() instanceof CheckBox) {
							CheckBox chk = (CheckBox) event.getSource();
							if (chk.isSelected()) {
								_nextButton.setDisable(false);
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


				Image image = new Image(file.toURI().toString(), 150, 100, false, false);
				ImageView iv = new ImageView(image);


				_imageList.add(new ImageTable(iv, checkBox, file.getAbsolutePath()));
			}
		}

		//set up columns in both tables
		_imageCol.setCellValueFactory(new PropertyValueFactory<ImageTable, ImageView>("Image"));
		_selectCol.setCellValueFactory(new PropertyValueFactory<ImageTable, CheckBox>("CheckBox"));
		_imageTable.setItems(_imageList);


	}


	@FXML
	public void onNextHandler(ActionEvent event) {


		ArrayList<String> imageList = new ArrayList<String>();

		for (ImageTable image : _imageList) {
			if (image.getCheckBox().isSelected()) {
				imageList.add(image.getFilePath());	
				System.out.println(image.getFilePath());

			}	
		}

		_creationProcess.setImageList(imageList);

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

		try {
			changeScene((Node)event.getSource(), "/fxml/CreateAudioScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}

	}

}
