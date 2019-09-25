package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Creation {
	public static String EXTENTION = ".mp4";
	public static String AUDIO_EXTENTION = ".wav";
	private static String DEFAULT_LENGTH = "???";
	
	
	private StringProperty name;
	private StringProperty length;
	
	
	public Creation(String n) {
		this(n, DEFAULT_LENGTH);
	}
	
	public Creation(String n, String l) {
		name = new SimpleStringProperty(n);
		length = new SimpleStringProperty(l);
	}
	
	public void setLength(String l) {
		length.set(l);
	}
	
	public String getName() {
		return name.get();
	}
	
	public String getLength() {
		return length.get();
	}
	
	@Override
	public String toString() {
		return (name.get() + ": " + length.get());
	}
	
	public static String getExtention() {
		return EXTENTION;
	}
}
