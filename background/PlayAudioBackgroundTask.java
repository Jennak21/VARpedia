package background;


import java.io.IOException;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

public class PlayAudioBackgroundTask extends Task<Void> {
	private String _audioName;
	private String _filePath;
	private Process _process;

	public PlayAudioBackgroundTask(String audioName, String filePath ) {
		_audioName = audioName;
		_filePath = filePath;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			String playAudio = "aplay " + _filePath + "\""+ _audioName + "\""+ Creation.AUDIO_EXTENTION;
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", playAudio);
			_process = processBuilder.start();
			_process.waitFor();

		} catch (IOException | InterruptedException e) {
		}

		return null;
	}
	
	public void stopProcess() {
		if (_process != null) {
			_process.destroy();
		}
	}
}

