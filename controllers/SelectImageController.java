package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SelectImageController extends SceneChanger implements Initializable{
	private CreationProcess _creationProcess;
	private String _allImagesFilePath = Main._FILEPATH +"/newCreation/allImages";

	@FXML
	private ProgressIndicator _progressIndicator;

	@FXML
	private Text _userText;

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


	private ObservableList<ImageTable> _imageList = FXCollections.observableArrayList();

	private DownloadImageBackgroundTask _downloadImage;




	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		_nextButton.setDisable(true);

		_creationProcess = CreationProcess.getInstance();

		_creationProcess.clearSelectedImages();


		_downloadImage = new DownloadImageBackgroundTask();
		Thread downloadImageThread = new Thread(_downloadImage);
		downloadImageThread.start();

		_downloadImage.setOnRunning(running -> {
			//make label visible which will show progress updates
			_progressIndicator.setProgress(-1.0);
			_userText.setText("Fetching images for your creation"); 	

		});


		_downloadImage.setOnSucceeded(finish -> {
			_userText.setText("Select images for your creation"); 	
			loadDataToTable();
			_progressIndicator.setVisible(false);
		});


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


		try {
			changeScene((Node)event.getSource(), "/fxml/FileNameScene.fxml") ;
		} catch (IOException e) {
			new ErrorAlert("Couldn't change scenes");
		}




	}

}
