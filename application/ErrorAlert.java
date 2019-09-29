package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Create and display error alert dialogue box 
 * @author mgur707
 *
 */
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
