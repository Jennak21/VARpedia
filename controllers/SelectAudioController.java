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
	
	private CreationProcesses _creationProcesses;
	
	
	
	
	@Override
	public void initialize (URL url, ResourceBundle resourceBundle){
		
		//TableColumn audioNameCol = new TableColumn("Audio Name");
		
		_nextButton.setDisable(true);
		_creationProcesses = CreationProcesses.getInstance();
		loadData();
	
	
		
		
		
		
//		for(int i = 0; i < 10; i ++) {
//			CheckBox cb = new CheckBox("hello");
//			_list.add(new TableSetterGetter("hello", cb));
//			
//		}
//		
//		
//		_audioTable.setItems(_list);
//		_audioName.setCellValueFactory(new PropertyValueFactory<TableSetterGetter, String>("_audioName"));
//		_select.setCellValueFactory(new PropertyValueFactory<TableSetterGetter, CheckBox>("_checkBox"));
//		
//		
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
//			            		int count= 0;
			            		if (audio.getCheckBox().isSelected()) {
			            			_nextButton.setDisable(false);
//			            			count ++ ;
			            			
			            			break;		
			            		}
//			            		if (count>0)  {
//			            			_nextButton.setDisable(false);
//			            		} else {
			            			_nextButton.setDisable(true); 
//			            		}
			            	}
			            	
			            }
			        }
			    }
			};
			
			checkBox.setOnAction(checkedHandler);
//			ChangeListener changeListener= new ChangeListener<Boolean>() {
//			    @Override
//			    public void changed(ObservableValue<? extends Boolean> ov,
//			        Boolean old_val, Boolean new_val) {
//			        if (new_val)
//			            colorlessCB.setSelected(false);
//			    }};

					
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
