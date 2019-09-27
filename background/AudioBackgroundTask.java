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
		
		try {
			String makeAudio = "text2wave -o " + Main._FILEPATH + "/newCreation/" + _filename + Creation.AUDIO_EXTENTION + " " + Main._FILEPATH + "/newCreation/text.txt -eval " + Main._FILEPATH + "/newCreation/settings.scm";
			int exitVal = BashCommandClass.runBashProcess(makeAudio);

			if (exitVal == 0) {
				return true;
			} else {
				return false;
			}
		}
		catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
