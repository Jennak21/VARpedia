package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Creation {
	public static String EXTENTION = ".mp4";
	public static String AUDIO_EXTENTION = ".wav";
	private static String DEFAULT_LENGTH = "???";
	
	private StringProperty _name;
	private StringProperty _length;
	
	/**
	 * Constructor
	 * @param name Creation name
	 */
	public Creation(String name) {
		this(name, DEFAULT_LENGTH);
	}
	
	/**
	 * Constructor
	 * @param name Creation name
	 * @param length Creation length
	 */
	public Creation(String name, String length) {
		_name = new SimpleStringProperty(name);
		_length = new SimpleStringProperty(length);
	}
	
	public void setLength(String length) {
		_length.set(length);
	}
	
	public String getName() {
		return _name.get();
	}
	
	public String getLength() {
		return _length.get();
	}
	
	@Override
	public String toString() {
		return (_name.get() + ": " + _length.get());
	}
	
	public static String getExtention() {
		return EXTENTION;
	}
}