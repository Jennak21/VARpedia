package controllers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * Abstract class for scene changing
 * @author Max Gurr & Jenna Kumar
 *
 */
public abstract class SceneChanger {
	/**
	 * Change scene to requested fxml path
	 * @param node - Component in current scene
	 * @param fxmlPath - Path to new scene
	 * @throws IOException
	 */
	public void changeScene(Node node, String fxmlPath) throws IOException {
		Parent sceneParent = FXMLLoader.load(getClass().getResource(fxmlPath));
		Scene scene = new Scene(sceneParent);
		Stage window = (Stage) node.getScene().getWindow();
		window.setScene(scene);
	}
}
