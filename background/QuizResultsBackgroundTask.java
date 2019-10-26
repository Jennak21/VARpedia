package background;

import controllers.QuizResult;

import java.io.IOException;
import java.util.List;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

/**
 * Background task for storing info of quiz results
 * @author Max Gurr & Jenna Kumar
 *
 */
public class QuizResultsBackgroundTask extends Task<Void> {
	List<QuizResult> _quizResults;
	
	public QuizResultsBackgroundTask(List<QuizResult> results) {
		_quizResults = results;
	}

	@Override
	/**
	 * Execution of task
	 */
	protected Void call() throws Exception {
		try {
			//Get everything currently in info file - in list format
			String getCreationInfoFromFile = "cat " + Main._CREATIONINFO;
			List<String> creationInfo = BashCommandClass.getListOutput(getCreationInfoFromFile);
			
			//String for all info to put into file
			String newFileInformation = "";
			
			//Go through each line of info currently in file
			for (String s: creationInfo) {
				//Extract each piece of info
				String[] info = s.split(";");
				String filename = info[0];
				String searchTerm = info[1];
				String length = info[2];
				String testAcc = info[3];
				
				//Check whether this creation was tested
				int creationIndex = getCreationIndex(filename);
				
				if (creationIndex != -1) {
					//Creation was tested, insert new info
					int oldTestAcc = Integer.parseInt(testAcc);
					int newTestAcc = oldTestAcc + _quizResults.get(creationIndex).getDeltaTestAcc();
															
					newFileInformation = newFileInformation + filename + ";" + searchTerm + ";" + length + ";" + newTestAcc + "\n";
					
					//Update creation info in storage list
					_quizResults.get(creationIndex).getCreation().setTestAcc(newTestAcc);
				} 
				else {
					//Creation wasn't tested, insert old info
					newFileInformation = newFileInformation + filename + ";" + searchTerm + ";" + length + ";" + testAcc + "\n";
				}
			}
			
			//Write all info to file
			String writeResultsToFile = "echo \"" + newFileInformation + "\" > " + Main._CREATIONINFO;
			BashCommandClass.runBashProcess(writeResultsToFile);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't get information");
		}
		
		
		return null;
	}
	
	/**
	 * Get index of creation in quiz results
	 * @param filename - Filename of creation to search for
	 * @return int - Index of creation (or -1 if doesn't exist)
	 */
	private int getCreationIndex(String filename) {
		for (QuizResult q: _quizResults) {
			if (q.sameFileCreation(filename)) {
				return _quizResults.indexOf(q);
			}
		}
		
		return -1;
	}

}
