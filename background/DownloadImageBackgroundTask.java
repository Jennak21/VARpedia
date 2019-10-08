package background;


import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import controllers.CreationProcess;
import controllers.ImageDownloader;
import javafx.concurrent.Task;

public class DownloadImageBackgroundTask extends Task<Boolean> {


	@Override
	protected Boolean call() throws Exception {
		
		//file path for directory where images will be stored
		String imgDir = Main._FILEPATH + "/newCreation/allImages";
		
		
	
		//run command to check if directory with images exists
		String command = "test -d " + imgDir;
		int num;
		try {
			num = BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
			return false;
		}

		//check exit code to determine if it exists, if so return
		if (num == 0 ) {		
			return true;
		} 
		
		//make directory to store images
		String makeImageDirCommand = "mkdir " + Main._FILEPATH  + "/newCreation/allImages";
	

		try {
			BashCommandClass.runBashProcess(makeImageDirCommand);
		} catch (IOException | InterruptedException e1) {
			return false;
		}


		try {
			ImageDownloader.getImages(CreationProcess.getInstance().getSearchTerm(), 10);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}

		return true;
	}


}



