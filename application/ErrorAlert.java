package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorAlert {
	
	public ErrorAlert(String message) {
		//Error dialogue box
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Error");
		alert.setContentText(message);

		alert.showAndWait();
	}
}
