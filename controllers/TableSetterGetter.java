package controllers;

import javafx.scene.control.CheckBox;

public class TableSetterGetter {
	String _audioName;
	CheckBox _checkBox;
	
	public TableSetterGetter(String audioName, CheckBox checkBox) {
		
		_audioName = audioName;
		_checkBox = checkBox;
	}

	
	
	public CheckBox getCheckBox() {
		return _checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		_checkBox = checkBox;
	}

	public String getAudioName() {
		return _audioName;
	}
	
	public void setAudioName(String audioName) {
		_audioName = audioName;
	}
	
	

}
