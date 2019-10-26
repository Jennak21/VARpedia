package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class for audio chunks used in video creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class AudioChunk {
	private int _chunkNum;
	private StringProperty _text;
	private StringProperty _voice;
	
	/**
	 * Constructor
	 * @param chunkNum - ID number for chunk
	 * @param text - Text used in chunk
	 * @param voice - Voice used for chunk
	 */
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
