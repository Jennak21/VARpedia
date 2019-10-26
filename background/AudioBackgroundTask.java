package background;

import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import javafx.concurrent.Task;

/**
 * Background task to create audio file
 * @author Max Gurr & Jenna Kumar
 *
 */
public class AudioBackgroundTask extends Task<Boolean> {
	private String _filename;
	private String _filepath;
	
	public AudioBackgroundTask(String filename, String filepath) {
		_filename = filename;
		_filepath = filepath;
	}

	@Override
	/**
	 * Execution of task
	 * @return Boolean - Success status of task
	 */
	protected Boolean call() {
		try {
			//Convert text to audio
			String makeAudio = "text2wave -o " + _filepath + _filename + Creation.AUDIO_EXTENTION + " " + Main._FILEPATH + "/newCreation/text.txt -eval " + Main._FILEPATH + "/newCreation/settings.scm";
			int exitVal = BashCommandClass.runBashProcess(makeAudio);
			
			//If command wasn't successful, return
			if (exitVal != 0) {
				return false;
			}
			
			//Check size of created file
			String checkLength = "stat -c%s " + _filepath + _filename + Creation.AUDIO_EXTENTION;
			String stringLength = BashCommandClass.getOutputFromCommand(checkLength);
			int intLength = Integer.parseInt(stringLength);
			//If created file is empty (0 bytes), return fail
			if (intLength == 0) {
				return false;
			}
			
			return true;
		}
		catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
