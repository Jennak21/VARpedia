package background;

import java.io.IOException;

import application.BashCommandClass;
import application.Main;
import controllers.CreationProcess;
import javafx.concurrent.Task;

/**
 * Background task for searching term on wikipedia and storing result
 * @author Max Gurr & Jenna Kumar
 *
 */
public class WikitBackgroundTask extends Task<Boolean> {
	
	private String _searchTerm;
	
	public WikitBackgroundTask(String term) {
		_searchTerm = term;
	}

	@Override
	/**
	 * Execution of background task
	 * @return Boolean - Whether or not search was successful
	 */
	protected Boolean call() {
		//Command to run wikipedia search
		String searchCommand = "echo \"\n\" | wikit " + _searchTerm + "; ";

		try {
			//Run command
			String searchResult = BashCommandClass.getOutputFromCommand(searchCommand);
			
			
			String notFound = "not found";
			String ambiguous = "Ambiguous results";

			//Check outcome of search
			if (searchResult.contains(notFound)) {
				//No results found for search
				updateMessage("No results");
				return false;
			} 
			else if (searchResult.contains(ambiguous)) {
				//Search returned ambiguous results
				updateMessage("Ambiguous");
				return false;
			} 
			else if (searchResult.isEmpty()) {
				//Nothing came back - empty
				updateMessage("Empty");
				return false; 
			} 
			else {
				//Search was successful
				
				//Store result
				CreationProcess creationProcess = CreationProcess.getInstance();
				creationProcess.setSearchTerm(_searchTerm);
				creationProcess.setSearchText(searchResult);
				
				//Make folder for new creation
				String newCreationFolder = "mkdir -p " + Main._FILEPATH + "/newCreation";
				BashCommandClass.runBashProcess(newCreationFolder);
				
				return true;
			}
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
