package background;

import java.io.IOException;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

public class PreviewBackgroundTask extends Task<Boolean> {
	private String _filename;
	private Process _process;
	
	public PreviewBackgroundTask(String filename) {
		_filename = filename;
	}
	
	@Override
	protected Boolean call() {
		try {
			String playAudio = "ffplay " + Main._FILEPATH + "/newCreation/" + _filename + ".mp4 -autoexit -nodisp";
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", playAudio);
			_process = processBuilder.start();
			_process.waitFor();
		
			return true;
							
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	public void stopProcess() {
		if (_process != null) {
			_process.destroy();
		}
	}
}
