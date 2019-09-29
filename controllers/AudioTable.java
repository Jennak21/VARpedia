package controllers;

public class AudioTable {

	String _audioName;

	public AudioTable(String audioName) {
		_audioName = audioName;
	}
	
	public String getAudioName() {
		return _audioName;
	}

	public void setAudioName(String audioName) {
		_audioName = audioName;
	}
}
