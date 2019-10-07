package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	public static final String _FILEPATH = System.getProperty("user.dir") + "/files";
	public static final String _VIDPATH=_FILEPATH + "/videos";
	public static final String _AUDIOPATH = _FILEPATH + "/audio";
	public static final String _CREATIONPATH = _FILEPATH + "/creations";
	
	@Override
	public void start(Stage primaryStage) {
		setup(); 
		
		//Load main menu
		Parent layout;
		try {
			layout = FXMLLoader.load(getClass().getResource("/fxml/MainMenuPane.fxml"));
			Scene scene = new Scene(layout);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("WikiSpeak");
			primaryStage.show();
		} catch (IOException e) {
			new ErrorAlert("Can't load application");
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Setup done at launch of application
	 */
	private void setup() {			
		//Set files folder directory path based on current working path
		
		//Create files folder if one doesn't exist
		String cmd = "mkdir -p " + _FILEPATH;
		try {
			int exitVal = BashCommandClass.runBashProcess(cmd);
			if (exitVal != 0) {
				new ErrorAlert("Something went wrong");
				Platform.exit();
			}
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
			Platform.exit();
		}
	}
}
