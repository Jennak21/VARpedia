package application;

/**
 * Singleton clas - Store a creation for use in a different scene
 * @author student
 *
 */
public class CreationStore {
	private static CreationStore CREATIONSTORE_INSTANCE = null;
	private Creation _creation;
		
	/**
	 * Singleton method
	 * @return
	 */
	public static CreationStore getInstance() {
		//Create new instance if one doesn't exist
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
