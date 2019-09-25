package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WarningAlert {
	
	public WarningAlert(String message) {
		//Error dialogue box
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("Warning");
		alert.setContentText(message);

		alert.showAndWait();
	}
}
