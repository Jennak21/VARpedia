package background;

import java.io.IOException;

import application.BashCommandClass;
import application.ErrorAlert;
import javafx.concurrent.Task;

public class PreviewBackgroundTask extends Task<Void> {
	private String _text;
	private Process process;
	
	public PreviewBackgroundTask(String text) {
		_text = text;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			String previewText = "echo \"" + _text + "\" | festival --tts";
			BashCommandClass.runBashProcess(previewText);
							
		} catch (IOException | InterruptedException e) {
		}
		
		return null;
	}
}
