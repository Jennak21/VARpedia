package controllers;

import application.Creation;

public class CreationStore {
	private static CreationStore CREATIONSTORE_INSTANCE = null;
	private Creation _creation;
		
	public static CreationStore getInstance() {
		if (CREATIONSTORE_INSTANCE == null) {
			CREATIONSTORE_INSTANCE = new CreationStore();
		}
		
		return CREATIONSTORE_INSTANCE;
	}
	
	public void setCreation(Creation creation) {
		_creation= creation;
	}
	
	public Creation getCreation() {
		return _creation;
	}
	
	public static void destroy() {
		CREATIONSTORE_INSTANCE = null;
	}
}
