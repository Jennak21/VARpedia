package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AudioChunk {
	private int _chunkNum;
	private StringProperty _text;
	private StringProperty _voice;
	
	public AudioChunk(int chunkNum, String text, String voice) {
		_chunkNum = chunkNum;
		_text = new SimpleStringProperty(text);
		_voice = new SimpleStringProperty(voice);
	}
	
	public String getText() {
		return _text.get();
	}
	
	public String getVoice() {
		return _voice.get();
	}
	
	public int getNum() {
		return _chunkNum;
	}
}
