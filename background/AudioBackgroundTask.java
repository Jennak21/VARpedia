package background;

import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import javafx.concurrent.Task;

public class AudioBackgroundTask extends Task<Boolean> {
	private String _filename;
	private String _filepath;
	
	public AudioBackgroundTask(String filename, String filepath) {
		_filename = filename;
		_filepath = filepath;
	}

	@Override
	protected Boolean call() {
		//Convert text to audio
		try {
			String makeAudio = "text2wave -o " + _filepath + _filename + Creation.AUDIO_EXTENTION + " " + Main._FILEPATH + "/newCreation/text.txt -eval " + Main._FILEPATH + "/newCreation/settings.scm";
			int exitVal = BashCommandClass.runBashProcess(makeAudio);
			
			//Read exit value of command and return relevant boolean
			if (exitVal != 0) {
				return false;
			}
			
			
			String checkLength = "stat -c%s " + _filepath + _filename + Creation.AUDIO_EXTENTION;
			String stringLength = BashCommandClass.getOutputFromCommand(checkLength);
			int intLength = Integer.parseInt(stringLength);
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
