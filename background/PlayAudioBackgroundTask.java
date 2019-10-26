package background;


import java.io.IOException;

import javafx.concurrent.Task;

/**
 * Background task for playing audio file
 * @author Max Gurr & Jenna Kumar
 *
 */
public class PlayAudioBackgroundTask extends Task<Void> {
	private String _audioName;
	private String _filePath;
	private Process _process;

	public PlayAudioBackgroundTask(String audioName, String filePath ) {
		_audioName = audioName;
		_filePath = filePath;
	}
	
	@Override
	/**
	 * Execution of task
	 */
	protected Void call() throws Exception {
		try {
			//Run command for playing given audio file
			String playAudio = "aplay " + _filePath + "\""+ _audioName + "\"";
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", playAudio);
			_process = processBuilder.start();
			_process.waitFor();

		} catch (IOException | InterruptedException e) {}

		return null;
	}
	
	/**
	 * Stop playback
	 */
	public void stopProcess() {
		if (_process != null) {
			_process.destroy();
		}
	}
	
	/**
	 * Check whether playback is running
	 * @return boolean - Whether playback is live
	 */
	public boolean isAlive() {
		if (_process != null) {
			return _process.isAlive();
		}
		return false;
	}
}

