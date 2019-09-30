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
	
	public AudioBackgroundTask(String filename) {
		_filename = filename;
	}

	@Override
	protected Boolean call() {
		//Convert text to audio
		try {
			String makeAudio = "text2wave -o " + Main._FILEPATH + "/newCreation/" + _filename + Creation.AUDIO_EXTENTION + " " + Main._FILEPATH + "/newCreation/text.txt -eval " + Main._FILEPATH + "/newCreation/settings.scm";
			int exitVal = BashCommandClass.runBashProcess(makeAudio);
			
			//Read exit value of command and return relevant boolean
			if (exitVal != 0) {
				return false;
			}
			
			String checkSize = "stat -c%s " + Main._FILEPATH + "/newCreation/" + _filename + Creation.AUDIO_EXTENTION;
			String stringSize = BashCommandClass.getOutputFromCommand(checkSize);
			int intSize = Integer.parseInt(stringSize);
			if (intSize <= 0) {
				return false;
			}
			
			return true;
		}
		catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
