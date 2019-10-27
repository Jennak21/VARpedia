package background;


import java.io.IOException;

import application.BashCommandClass;
import application.CreationProcess;
import application.ImageDownloader;
import application.Main;
import customUI.ErrorAlert;
import javafx.concurrent.Task;

/**
 * Background task for fetching images from Flickr
 * @author Max Gurr & Jenna Kumar
 *
 */
public class DownloadImageBackgroundTask extends Task<Boolean> {
	
	@Override
	/**
	 * Execution of task
	 * @return Boolean - Success status of task
	 */
	protected Boolean call() throws Exception {
		//File path for directory where images will be stored
		String imgDir = Main._FILEPATH + "/newCreation/allImages";
		
		//Run command to check if directory with images exists
		String command = "test -d " + imgDir;
		int num;
		try {
			num = BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Something went wrong");
			return false;
		}

		//Check exit code to determine if it exists, if so return
		if (num == 0 ) {		
			return true;
		} 
		
		//Make directory to store images
		String makeImageDirCommand = "mkdir " + Main._FILEPATH  + "/newCreation/allImages";
		try {
			BashCommandClass.runBashProcess(makeImageDirCommand);
		} catch (IOException | InterruptedException e1) {
			return false;
		}
		
		//Get images from Flickr
		try {
			ImageDownloader.getImages(CreationProcess.getInstance().getSearchTerm(), 10);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}



