package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JOptionPane;

import application.AudioChunk;
import javafx.concurrent.Task;

public class CreationProcess {
	private static CreationProcess CREATION_INSTANCE = null;


	private String _searchTerm = "apple";
	private String _fullSearchText = "";
	private String _fullUserText = "";
	private String _selectedText = "";
	private String _fileName;
	private String _bgMusic;
	private List<AudioChunk> _audioChunkList = new ArrayList<AudioChunk>();
	private int _numChunks = 0;
	private ArrayList<String> _imageList;

	public static CreationProcess getInstance() {
		if (CREATION_INSTANCE == null) {
			CREATION_INSTANCE = new CreationProcess(); 
		} 
		return CREATION_INSTANCE; 
	}

	public void addAudioChunk(AudioChunk newChunk) {
		_audioChunkList.add(newChunk);
		_numChunks++;
	}
	
	public void deleteAudioChunk(AudioChunk chunk) {
		_audioChunkList.remove(chunk);
	}

	public List<AudioChunk> getAudioChunks() {
		return _audioChunkList;
	}

	public int getNumChunks() {
		return _numChunks;
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
	
	public void addToUserText(String s) {
		_fullUserText = _fullUserText + s;
	}

	public void resetUserText() {
		_fullUserText = "";
	}
	
	public String getUserText() {
		return _fullUserText;
	}

	public void setImageList(ArrayList<String> imageList) {
		_imageList= imageList;
		
	}
	
	public ArrayList<String> getImageList() {
		return _imageList;	
	}
	
	public void setBGMusic(String music) {
		_bgMusic = music;
	}
	
	public String getBGMusic() {
		return _bgMusic;
	}

	public void clearSelectedImages() {
		_imageList = null;
	}

	public static void destroy() {
		CREATION_INSTANCE = null;
	}
}
