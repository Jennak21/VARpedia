package background;

import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.InformationAlert;
import application.Main;
import javafx.concurrent.Task;

public class AudioBackgroundTask extends Task<Boolean> {
	private String _text;
	private String _voice;
	private String _filename;
	
	public AudioBackgroundTask(String text, String voice, String filename) {
		_text = text;
		_voice = voice;
		_filename = filename;
	}

	@Override
	protected Boolean call() {
		
		try {
			String settingsFile = "echo \"(" + _voice + ")\" > " + Main._FILEPATH + "/newCreation/settings.scm";
			BashCommandClass.runBashProcess(settingsFile);
			String textFile = "echo \"" + _text + "\" > " + Main._FILEPATH + "/newCreation/text.txt";
			BashCommandClass.runBashProcess(textFile);
			
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
