package controllers;

import application.Creation;

public class MediaProcess {
	private static MediaProcess MEDIA_INSTANCE = null;
	private Creation _creation;
		
	public static MediaProcess getInstance() {
		if (MEDIA_INSTANCE == null) {
			MEDIA_INSTANCE = new MediaProcess();
		}
		
		return MEDIA_INSTANCE;
	}
	
	public void setCreation(Creation creation) {
		_creation= creation;
	}
	
	public Creation getCreation() {
		return _creation;
	}
	
	public static void destroyProcess() {
		MEDIA_INSTANCE = null;
	}
}
