package background;

import java.io.IOException;
import java.util.List;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

public class DeleteCreationBackgroundTask extends Task<Boolean> {
	private String _filename;
	
	public DeleteCreationBackgroundTask(String filename) {
		_filename = filename;
	}

	@Override
	protected Boolean call() {
		try {
			deleteFiles();
			deleteCreationInfo();
			
			return true;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	private void deleteFiles() throws IOException, InterruptedException {
	
			String creationFile = Main._CREATIONPATH + "/" + _filename + Creation.EXTENTION;
			String videoFile = Main._VIDPATH + "/" + _filename + Creation.EXTENTION;
			String audioFile = Main._AUDIOPATH + "/" + _filename + Creation.AUDIO_EXTENTION;
			String deleteFiles = "rm -f " + creationFile + " " + videoFile + " " + audioFile;
			
			BashCommandClass.runBashProcess(deleteFiles);
	}
	
	private void deleteCreationInfo() throws IOException, InterruptedException {
		String getCreationInfoFromFile = "cat " + Main._CREATIONINFO;
		List<String> creationInfo = BashCommandClass.getListOutput(getCreationInfoFromFile);
		
		String newFileInformation = "";
		
		for (String s: creationInfo) {
			String[] info = s.split(";");
			
			String filename = info[0];
			String searchTerm = info[1];
			String length = info[2];
			String testAcc = info[3];
			
			if (!_filename.equals(filename)) {
				newFileInformation = newFileInformation + s + "\n";
			}
		}
		
		String writeResultsToFile = "echo \"" + newFileInformation + "\" > " + Main._CREATIONINFO;
		BashCommandClass.runBashProcess(writeResultsToFile);
		
		Main.deleteCreation(_filename);
	}
}
