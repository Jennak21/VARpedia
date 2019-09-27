package background;

import java.io.IOException;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

public class PreviewBackgroundTask extends Task<Boolean> {
	private String _text;
	private Process _process;
	
	public PreviewBackgroundTask() {
		//_text = text;
	}
	
	@Override
	protected Boolean call() {
		try {
			String playAudio = "ffplay " + Main._FILEPATH + "/newCreation/tempAudio.mp4 -autoexit -nodisp";
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
