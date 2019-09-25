package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InformationAlert {
	
	public InformationAlert(String message) {
		//Error dialogue box
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}
}
