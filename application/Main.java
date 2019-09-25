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
	public static String _FILEPATH;
	
	@Override
	public void start(Stage primaryStage) {
		//New
		setup(); 
		
		Parent layout;
		try {
			layout = FXMLLoader.load(getClass().getResource("/fxml/MainMenuPane.fxml"));
			Scene scene = new Scene(layout);
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
	
	private void setup() {			
		//Set files folder directory path based on current working path
		String dir = System.getProperty("user.dir");
		_FILEPATH = dir + "/files";
		
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
