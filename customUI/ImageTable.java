package customUI;

import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;

/**
 * Image table for selecting images
 * @author Max Gurr & Jenna Kumar
 *
 */
public class ImageTable {
	ImageView _image;
	CheckBox _checkBox;
	String _filePath;

	public ImageTable(ImageView image, CheckBox checkBox, String filePath) {
		_image = image;
		_checkBox = checkBox;
		_filePath = filePath;
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
	
	public void setFilePath (String filePath) {
		_filePath = filePath;
	}
	
	public String getFilePath() {
		return _filePath;
		
	}
}
