package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class for full creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class Creation {
	public static String EXTENTION = ".mp4";
	public static String AUDIO_EXTENTION = ".wav";
	private static String DEFAULT_LENGTH = "???";
	private static String DEFAULT_ACC = "0";
	
	private StringProperty _filename;
	private StringProperty _searchTerm;
	private StringProperty _length;
	private StringProperty _testAcc;
	
	
	/**
	 * Constructor
	 * @param name Creation name
	 */
	public Creation(String filename, String searchTerm, String length) {
		this(filename, searchTerm, length, DEFAULT_ACC);
	}
	
	/**
	 * Constructor
	 * @param name Creation name
	 * @param length Creation length
	 */
	public Creation(String filename, String searchTerm, String length, String testAcc) {
		_filename = new SimpleStringProperty(filename);
		_searchTerm = new SimpleStringProperty(searchTerm);
		_length = new SimpleStringProperty(length);
		_testAcc = new SimpleStringProperty(testAcc);
	}
	
	public void setLength(String length) {
		_length.set(length);
	}
	public String getLength() {
		return _length.get() + "s";
	}
	
	public String getFilename() {
		return _filename.get();
	}
	
	public String getSearchTerm() {
		return _searchTerm.get();
	}
	
	public void setTestAcc(int newAcc) {
		_testAcc = new SimpleStringProperty("" + newAcc);
	}
	public String getTestAcc() {
		return _testAcc.get() + "%";
	}
	
	public int getIntAcc() {
		return Integer.parseInt(_testAcc.get());
	}
	
	/**
	 * Get accuracy weighting to use for quiz question selection
	 */
	public int getAccuracyWeighting() {
		int accuracyWeight = 115 - Integer.parseInt(_testAcc.get());
		return accuracyWeight;
	}
	
	@Override
	public String toString() {
		return (_filename.get() + ": " + _length.get());
	}
}