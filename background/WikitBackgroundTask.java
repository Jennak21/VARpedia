package background;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import controllers.CreationProcess;
import javafx.concurrent.Task;

public class WikitBackgroundTask extends Task<Boolean> {
	
	private String _searchTerm;
	
	public WikitBackgroundTask(String term) {
		_searchTerm = term;
	}

	@Override
	protected Boolean call() {
		//Command to run wikipedia search
		String searchCommand = "echo \"\n\" | wikit " + _searchTerm + "; ";

		try {
			String searchResult = BashCommandClass.getOutputFromCommand(searchCommand);
			
			String notFound = "not found";
			String ambiguous = "Ambiguous results";
			
			//If search wasn't successful, quit
			if (searchResult.contains(notFound)) {
				updateMessage("No results");
				return false;
			} else if (searchResult.contains(ambiguous)) {
				updateMessage("Ambiguous");
				return false;
			} else if (searchResult.isEmpty()) {
				updateMessage("Empty");
				return false; 
			} else {
				CreationProcess creationProcess = CreationProcess.getInstance();
				creationProcess.setSearchTerm(_searchTerm);
				creationProcess.setSearchText(searchResult);
				
				String newCreationFolder = "mkdir -p " + Main._FILEPATH + "/newCreation";
				BashCommandClass.runBashProcess(newCreationFolder);
				
				return true;
			}
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
