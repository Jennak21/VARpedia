package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.event.ChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class SelectAudioController extends SceneChanger implements Initializable{
	
	
	
	@FXML 
	private TableColumn<TableSetterGetter, String> _audioName;
	
	@FXML 
	private TableColumn<TableSetterGetter, CheckBox> _select;
	
	@FXML
	private TableView<TableSetterGetter> _audioTable;
	
	@FXML
	private Button _nextButton;
	
	@FXML
	private Button _backButton;
	
	ObservableList<TableSetterGetter> _list = FXCollections.observableArrayList();
	
	private CreationProcess _creationProcesses;
	
	
	
	
	@Override
	public void initialize (URL url, ResourceBundle resourceBundle){
		
		_nextButton.setDisable(true);
		_creationProcesses = CreationProcess.getInstance();
		loadData();
			
	}	
	
	public void loadData() {
		ArrayList<String> audioNames = _creationProcesses.getAudioNames();	
		
		for (String audioName : audioNames) {
			
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
			            	for (TableSetterGetter audio : _list) {
			            		if (audio.getCheckBox().isSelected()) {
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


					
			_list.add(new TableSetterGetter(audioName, checkBox));
			
		}
		
		//_creationList.getItems().addAll( _observeList);
	
		System.out.println(_audioName);
		
		_audioName.setCellValueFactory(new PropertyValueFactory<TableSetterGetter, String>("AudioName"));
		_select.setCellValueFactory(new PropertyValueFactory<TableSetterGetter, CheckBox>("CheckBox"));
	
		_audioTable.setItems(_list);
	} 
	
		
	
	
	@FXML
	private void displayButtons(MouseEvent event) {
		TableSetterGetter audio = _audioTable.getSelectionModel().getSelectedItem();
		if (audio.getCheckBox() == null ) {
			_nextButton.setDisable(true);
		}else {
			_nextButton.setDisable(false);	 
		}
	}
	
	
	@FXML
	private void onBackHandler(ActionEvent event) {
		
	}

	@FXML
	private void onNextHandler(ActionEvent event) {
		
//		ObservableList<TableSetterGetter> audioSelectedList = FXCollections.observableArrayList();
		ArrayList<String> audioSelectedList = new ArrayList<String>();
		
		for (TableSetterGetter audio : _list) {
			if (audio.getCheckBox().isSelected()) {
//				audioSelectedList.add(audio);
				audioSelectedList.add(audio.getAudioName());
			}
		}
		
		_creationProcesses.createCombinedAudio(audioSelectedList);
		
		
		//System.out.println(audList);
		
	}
}
