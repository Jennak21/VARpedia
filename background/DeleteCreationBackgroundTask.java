package background;

import java.io.IOException;
import java.util.List;

import application.BashCommandClass;
import application.Creation;
import application.Main;
import javafx.concurrent.Task;

/**
 * Background task for deleting creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class DeleteCreationBackgroundTask extends Task<Boolean> {
	private String _filename;
	
	public DeleteCreationBackgroundTask(String filename) {
		_filename = filename;
	}

	@Override
	/**
	 * Execution for task
	 * @return Boolean - Success status of task
	 */
	protected Boolean call() {
		try {
			deleteFiles();
			deleteCreationInfo();
			
			return true;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	/**
	 * Delete all files related to creation
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void deleteFiles() throws IOException, InterruptedException {
		//Construct and run command
		String creationFile = Main._CREATIONPATH + "/" + _filename + Creation.EXTENTION;
		String videoFile = Main._VIDPATH + "/" + _filename + Creation.EXTENTION;
		String audioFile = Main._AUDIOPATH + "/" + _filename + Creation.AUDIO_EXTENTION;
		String deleteFiles = "rm -f " + creationFile + " " + videoFile + " " + audioFile;
		
		BashCommandClass.runBashProcess(deleteFiles);
	}
	
	/**
	 * Delete creation info from file
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void deleteCreationInfo() throws IOException, InterruptedException {
		//Get all text in file
		String getCreationInfoFromFile = "cat " + Main._CREATIONINFO;
		List<String> creationInfo = BashCommandClass.getListOutput(getCreationInfoFromFile);
		
		//String for all new text
		String newFileInformation = "";
		
		//Go through each line from text file
		for (String s: creationInfo) {
			//Extract info
			String[] info = s.split(";");
			String filename = info[0];
			
			//If current line represents creation to delete, don't include in new text
			if (!_filename.equals(filename)) {
				newFileInformation = newFileInformation + s + "\n";
			}
		}
		
		//Write all new text to file
		String writeResultsToFile = "echo \"" + newFileInformation + "\" > " + Main._CREATIONINFO;
		BashCommandClass.runBashProcess(writeResultsToFile);
		
		//Delete creation from storage list
		Main.deleteCreation(_filename);
	}
}
