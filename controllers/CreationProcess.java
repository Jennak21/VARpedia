package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JOptionPane;

import javafx.concurrent.Task;

public class CreationProcess {
	private static CreationProcess CREATION_INSTANCE= null;

	
	private String _searchTerm;
	private String _fullSearchText;
	private String _fullUserText;
	private String _selectedText;
	public String _fileName;

	public static CreationProcess getInstance() {
		if (CREATION_INSTANCE == null) {
			CREATION_INSTANCE = new CreationProcess(); 
		} 
		return CREATION_INSTANCE; 
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
	
	public void setFileName(String  name) throws ExistingFileException {	
		_fileName = name;	

		if (fileExists(name)) {
			throw new ExistingFileException("");
		}
	}

	public String getFileName() {
		return _fileName;
	}
	
//	public void createVideo() throws UserInputExceptions {
//
//		//get selected lines specified by the user
//		StringBuilder stringBuilder = new StringBuilder();
//
//		for(int i = 0; i< (_numLinesInt); i++){
//			String numberedLine = _fullTextArray[i] + "\n";
//			stringBuilder.append(numberedLine);
//		}
//
//		//		String selectedText = stringBuilder.toString();
//		_selectedText = stringBuilder.toString();
//
//	
//		Thread thread = new Thread(new BackgroundVidWorker());
//		thread.start();
//
//	}


	public boolean fileExists(String fileName) {

		//run command to check if file name exists
		String command = "test -f creations/\"" + fileName + "\".mp4";
		int num = runBashProcess(command);

		//check exit code to determine if file exists
		if (num == 0 ) {		
			return true;
		} else {
			return false;
		}

	}

	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}
		return true;
	}
	
	public ArrayList<String> getAudioNames () {
		//create a text file that stores file names
		String createNameTextCommand = "ls -1a ./creations/*.wav | sed -r \"s/.+\\/(.+)\\..+/\\1/\" > AudioList.txt";
		runBashProcess(createNameTextCommand);
		
		//read from the text file
		ArrayList<String> lines = null;
		try {
			 lines = new ArrayList<>(Files.readAllLines(Paths.get("AudioList.txt")));
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Processing error");
			System.exit(0);
		}	
		return lines;		
	}
	
	public void createCombinedAudio (ArrayList<String> selectedAudio) {
		String combineAudioCommand = "sox ";
		for (String audioName  : selectedAudio) {
			combineAudioCommand = combineAudioCommand + "./creations/" + audioName + ".wav ";
			
		}
		
		combineAudioCommand = combineAudioCommand + "completeAudio.wav";
		
		System.out.println(combineAudioCommand);
		runBashProcess(combineAudioCommand);
		
	}



	public void searchOnWikit(String term) throws UserInputExceptions {

		String searchCommand = "wikit " +term;
		String text = getOutputFromCommand(searchCommand);
		
		//check for invalid search
		if (text.contains("not found :^(")) {
			throw new UserInputExceptions("Invalid search term, please try another term");

		}

		_searchTerm = term;
		_fullSearchText = text;

	}

	public void deleteExistingFile () {
		String command = "rm ./creations/\"" + _fileName + "\".mp4";
		runBashProcess (command);
	}


	private class BackgroundVidWorker extends Task<Void> {

		@Override
		protected Void call() {


			String newFilesAndAudioCommand = "mkdir -p temp; mkdir -p creations; echo \"" +  _selectedText + "\" |"
					+ " text2wave -o ./temp/tempAudio.wav; "; 

			String makeVideoCommand = "ffmpeg -f lavfi -i color=c=blue:s=320x240:d=`soxi -D ./temp/tempAudio.wav` -vf"
					+" \"drawtext=fontfile=Montserrat-Regular.ttf:fontsize=30: \\fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" 
					+ _searchTerm + "'\" ./temp/tempVideo.mp4 &> /dev/null; ";

			String creationVideoCommand = "ffmpeg -i ./temp/tempVideo.mp4 -i ./temp/tempAudio.wav -c:v copy -c:a aac -strict" +
					" experimental ./creations/" + _fileName + ".mp4 &> /dev/null; ";
			String deleteTempFileCommand = "rm -r temp";
			String command = newFilesAndAudioCommand + makeVideoCommand + creationVideoCommand + deleteTempFileCommand;

			runBashProcess(command);

			return null;

		}	


	}


	public int runBashProcess (String command) {
		List<String> commands;

		commands = new ArrayList<>();
		commands.add("bash");
		commands.add("-c");

		commands.add(2, command);

		// Run BASH process
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			Process process;
			try {
				process = processBuilder.start();
				process.waitFor();
			
				//get exit status
				int status = process.exitValue();
				return status;
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
			return -1;
	}

	private static String getOutputFromCommand(String command) {

		List<String> commands;

		commands = new ArrayList<>();
		commands.add("bash");
		commands.add("-c");


		commands.add(2, command);

		StringBuffer output = new StringBuffer();

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			Process process;
			try {
				process = processBuilder.start();
			
				int status;
				status = process.waitFor();
			

				if (status != 0) {
					return " ";
				}
	
				//read output and append to string
				BufferedReader stdOut = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
	
				String line = "";			
				while ((line = stdOut.readLine())!= null) {
					output.append(line + "\n");
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return output.toString();

	}

	public static void destroy() {
		CREATION_INSTANCE = null;
	}
}
