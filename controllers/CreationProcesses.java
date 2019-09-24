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

public class CreationProcesses {
	private static CreationProcesses CREATION_INSTANCE= null;

	private String[] _fullTextArray;
	private String _searchTerm;
	public String _fullNumberedText;
	public String _fileName;
	private String _selectedText;
	public int _numLinesInt;


	private CreationProcesses() {}

	public static CreationProcesses getInstance() {
		if (CREATION_INSTANCE == null) {
			CREATION_INSTANCE = new CreationProcesses(); 
		} 
		return CREATION_INSTANCE; 
	}
	
	public void createVideo() throws UserInputExceptions {

		//get selected lines specified by the user
		StringBuilder stringBuilder = new StringBuilder();

		for(int i = 0; i< (_numLinesInt); i++){
			String numberedLine = _fullTextArray[i] + "\n";
			stringBuilder.append(numberedLine);
		}

		//		String selectedText = stringBuilder.toString();
		_selectedText = stringBuilder.toString();

	
		Thread thread = new Thread(new BackgroundVidWorker());
		thread.start();

	}

	public void setNameandNumber (String numLines, String  name) throws UserInputExceptions, ExistingFileException {

		if (!isInteger(numLines)) {
			throw new UserInputExceptions("Please specify an integer");
		} 
		int numLinesInt = Integer.parseInt(numLines);

		if ((numLinesInt < 1) || (numLinesInt > _fullTextArray.length) ) {
			throw new UserInputExceptions("Please specify a number within the valid range ");			
		}
		_numLinesInt= numLinesInt;		
		_fileName = name;	

		if (fileExists(name)) {
			throw new ExistingFileException("");
		}

	}

	public String getFileName() {
		return _fileName;
	}

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
		
		System.out.println(text);

		//check for invalid search
		if (text.contains("not found :^(")) {
			throw new UserInputExceptions("Invalid search term, please try another term");

		}

		_searchTerm = term;


		
		String replacedText = text.replace('.','\n').replace('?', '\n');
		
		System.out.println(replacedText);

		//split lines of the test into array
		String lines[] = replacedText.split("\\r?\\n");
		_fullTextArray = lines;

		StringBuilder numberedString = new StringBuilder();

		//add numbers to each line and combine lines in a string
		for(int i = 0; i< lines.length; i++){
			int num = i + 1;
			String numberedLine = num + ": " + lines[i] + "\n";

			numberedString.append(numberedLine);
		}
		_fullNumberedText = numberedString.toString();

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


	public String getNumberedText() {
		return _fullNumberedText;
	}

	public int runBashProcess (String command) {
		List<String> commands;

		commands = new ArrayList<>();
		commands.add("bash");
		commands.add("-c");

		commands.add(2, command);

		// Run BASH process
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			Process process = processBuilder.start();
			process.waitFor();
			//get exit status
			int status = process.exitValue();
			return status;


		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Processing error");
			System.exit(0);
			return 0;

		}


	}

	private static String getOutputFromCommand(String command) {

		List<String> commands;

		commands = new ArrayList<>();
		commands.add("bash");
		commands.add("-c");


		commands.add(2, command);

		StringBuffer output = new StringBuffer();

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			Process process = processBuilder.start();
			int status = process.waitFor();

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


		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Processing error");
			System.exit(0);
		}

		return output.toString();

	}

}
