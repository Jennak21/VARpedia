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

		//make directory for vid files

		String makeImageDirCommand = "mkdir " + Main._FILEPATH  + "/" + "newCreation" + "/" + "allImages";
	

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



