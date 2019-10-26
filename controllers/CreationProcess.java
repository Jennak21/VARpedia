package controllers;

import java.util.ArrayList;
import java.util.List;
import application.AudioChunk;

/**
 * Singleton class for storing information during process of creating a new creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class CreationProcess {
	private static CreationProcess CREATION_INSTANCE = null;
	private String _searchTerm = "apple";
	private String _fullSearchText = "";
	private String _fileName;
	private String _bgMusic;
	private List<AudioChunk> _audioChunkList = new ArrayList<AudioChunk>();
	private int _numChunks = 0;
	private ArrayList<String> _imageList;

	/**
	 * Singleton method
	 * @return CreationProcess - Singleton instance
	 */
	public static CreationProcess getInstance() {
		//Create new instance if none exists
		if (CREATION_INSTANCE == null) {
			CREATION_INSTANCE = new CreationProcess(); 
		} 
		
		return CREATION_INSTANCE; 
	}
	
	/**
	 * Add new audio chunk to list
	 * @param newChunk - New audio chunk to add
	 */
	public void addAudioChunk(AudioChunk newChunk) {
		_audioChunkList.add(newChunk);
		_numChunks++;
	}
	
	/**
	 * Delete audio chunk from list
	 * @param chunk - Chunk to delete
	 */
	public void deleteAudioChunk(AudioChunk chunk) {
		_audioChunkList.remove(chunk);
	}

	public List<AudioChunk> getAudioChunks() {
		return _audioChunkList;
	}

	public int getNumChunks() {
		return _numChunks;
	}
	
	/**
	 * Get number of images stored
	 */
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
	
	public void setBGMusic(String music) {
		_bgMusic = music;
	}
	
	public String getBGMusic() {
		return _bgMusic;
	}

	public void clearSelectedImages() {
		_imageList = null;
	}

	/**
	 * Destroy singleton instance
	 */
	public static void destroy() {
		CREATION_INSTANCE = null;
	}
}
