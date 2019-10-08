package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JOptionPane;

import javafx.concurrent.Task;

public class CreationProcess {
	private static CreationProcess CREATION_INSTANCE = null;


	private String _searchTerm = "apple";
	private String _fullSearchText = "";
	private String _fullUserText = "";
	private String _selectedText = "";
	public String _fileName;
	public int _numImages;
	public ArrayList<String> _audioFileList;

	public static CreationProcess getInstance() {
		if (CREATION_INSTANCE == null) {
			CREATION_INSTANCE = new CreationProcess(); 
		} 
		return CREATION_INSTANCE; 
	}

	public void setAudioFiles(ArrayList<String> audioFileList) {
		_audioFileList = audioFileList;
	}

	public ArrayList<String> getAudioFiles() {
		return _audioFileList;
	}

	public void setNumImages(int numImages) {
		_numImages = numImages;
	}

	public int getNumImages() {
		return _numImages;
	}


	public void setSearchTerm(String term) {
		_searchTerm = term;
	}

	public String getSearchTerm() {
		return _searchTerm;
	}

	public void setSearchText(String text) {
		_fullSearchText = text;
	}

	public String getSearchText() {
		return _fullSearchText;
	}

	public void setFileName(String  name){	
		_fileName = name;	
	}

	public String getFileName() {
		return _fileName;
	}
	
	public void addToUserText(String s) {
		_fullUserText = _fullUserText + s;
	}

	public void resetUserText() {
		_fullUserText = "";
	}
	
	public String getUserText() {
		return _fullUserText;
	}


	public static void destroy() {
		CREATION_INSTANCE = null;
	}
}
