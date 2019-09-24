package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	public static String _FILEPATH;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		//New
		setup(); 
		
		//Parent layout = FXMLLoader.load(getClass().getResource("/fxml/MainMenuPane.fxml"));
		Parent layout = FXMLLoader.load(getClass().getResource("/fxml/SelectAudioScene.fxml"));
		Scene scene = new Scene(layout);
		primaryStage.setScene(scene);
		primaryStage.setTitle("WikiSpeak");
		primaryStage.show();
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
		ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
		
		try {
			Process process = builder.start();
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
