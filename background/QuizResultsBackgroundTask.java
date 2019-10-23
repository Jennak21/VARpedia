package background;

import controllers.QuizResult;

import java.io.IOException;
import java.util.List;

import application.BashCommandClass;
import application.ErrorAlert;
import application.Main;
import javafx.concurrent.Task;

public class QuizResultsBackgroundTask extends Task<Void> {
	List<QuizResult> _quizResults;
	
	public QuizResultsBackgroundTask(List<QuizResult> results) {
		_quizResults = results;
	}

	@Override
	protected Void call() throws Exception {
		try {
			String getCreationInfoFromFile = "cat " + Main._CREATIONINFO;
			List<String> creationInfo = BashCommandClass.getListOutput(getCreationInfoFromFile);
			
			String newFileInformation = "";
			
			for (String s: creationInfo) {
				String[] info = s.split(";");
				
				String filename = info[0];
				String searchTerm = info[1];
				String length = info[2];
				String testAcc = info[3];
				
				int creationIndex = getCreationIndex(filename);
				
				
				if (creationIndex != -1) {
					int oldTestAcc = Integer.parseInt(testAcc);
					int newTestAcc = oldTestAcc + _quizResults.get(creationIndex).getDeltaTestAcc();
															
					newFileInformation = newFileInformation + filename + ";" + searchTerm + ";" + length + ";" + newTestAcc + "\n";
					
					_quizResults.get(creationIndex).getCreation().setTestAcc(newTestAcc);
				} else {
					newFileInformation = newFileInformation + filename + ";" + searchTerm + ";" + length + ";" + testAcc + "\n";
				}
			}
			
			String writeResultsToFile = "echo \"" + newFileInformation + "\" > " + Main._CREATIONINFO;
			BashCommandClass.runBashProcess(writeResultsToFile);
		} catch (IOException | InterruptedException e) {
			new ErrorAlert("Couldn't get information");
		}
		
		
		return null;
	}
	
	private int getCreationIndex(String filename) {
		for (QuizResult q: _quizResults) {
			if (q.sameFileCreation(filename)) {
				return _quizResults.indexOf(q);
			}
		}
		
		return -1;
	}

}
