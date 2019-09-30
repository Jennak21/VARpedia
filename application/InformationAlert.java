package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Create and display information alert dialogue box
 * @author mgur707
 *
 */
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
