package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public abstract class SceneChanger {
	public void changeScene(Node node, String fxmlPath) throws IOException {
		Parent sceneParent = FXMLLoader.load(getClass().getResource(fxmlPath));
		Scene scene = new Scene(sceneParent);
		Stage window = (Stage) node.getScene().getWindow();
		window.setScene(scene);
	}
}
