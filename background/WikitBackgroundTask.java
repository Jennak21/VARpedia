package background;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.Main;
import controllers.CreationProcess;
import javafx.concurrent.Task;

public class WikitBackgroundTask extends Task<Boolean> {
	
	private String _searchTerm;
	
	public WikitBackgroundTask(String term) {
		_searchTerm = term;
	}

	@Override
	protected Boolean call() throws Exception {
		//Command to run wikipedia search
		String searchResults = "echo \"\n\" | wikit " + _searchTerm;
		
		ProcessBuilder builder = new ProcessBuilder("bash", "-c", searchResults);
		
		try {
			Process process =  builder.start();

			process.waitFor();
						
			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String searchResult = "";
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null ) {
				searchResult = searchResult + line;
			}
						
			String notFound = "not found";
			String ambiguous = "Ambiguous results";
			
			//If search wasn't successful, quit
			if (searchResult.contains(notFound) || searchResult.contains(ambiguous)) {
				return false; 
			} else {
				CreationProcess creationProcess = CreationProcess.getInstance();
				creationProcess.setSearchTerm(_searchTerm);
				creationProcess.setSearchText(searchResult);
				
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}
}
