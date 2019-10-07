package controllers;

import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;

public class ImageTable {

	ImageView _image;
	CheckBox _checkBox;

	public ImageTable(ImageView image, CheckBox checkBox) {
		_image = image;
		_checkBox = checkBox;
	}
	
	public ImageView getImage() {
		return _image;
	}

	public void setImage(ImageView image) {
		_image = image;
	}
	
	public CheckBox getCheckBox() {
		return _checkBox;
	}

	public void setCheckBox (CheckBox checkBox) {
		_checkBox = checkBox;
	}
}
