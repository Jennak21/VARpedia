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
	private static CreationProcess CREATION_INSTANCE= null;


	private String _searchTerm = "apple";
	private String _fullSearchText;
	public String _fileName;
	public ArrayList<String> _audioFileList;
	public ArrayList<String> _imageList;

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


	public int getNumImages() {
		if (_imageList != null) {
			return _imageList.size();
		} else {
			return 0;
		}
		
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
	
	public void setImageList(ArrayList<String> imageList) {
		_imageList= imageList;
		
	}
	

	public ArrayList<String> getImageList() {
		return _imageList;	
	}


	public void clearSelectedImages() {
		_imageList = null;
	}



	public static void destroy() {
		CREATION_INSTANCE = null;
	}
}
