package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.BashCommandClass;
import application.Main;
import javafx.fxml.Initializable;


public class CreatingVideoContoller extends SceneChanger implements Initializable{ 

	private CreationProcess _creationProcess;
	private String _searchTerm;
	private int _numImages;
	private String _fileName;
	private String _filePath = Main._FILEPATH +"/newCreations/";
	private String _tempAudioFilePath;
	private String _tempVidFilePath;
	private String _creationFilePath;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_creationProcess = CreationProcess.getInstance();
		_searchTerm = _creationProcess.getSearchTerm();
		_numImages = _creationProcess.getNumImages();
		_fileName= _creationProcess.getFileName();
	
		// TODO Auto-generated method stub
		
		_tempAudioFilePath = _filePath + _fileName +"TempAudio.wav";
		_tempVidFilePath = _filePath  + _fileName + "TempVideo.mp4";
		_creationFilePath = Main._FILEPATH + "/" + _fileName + ".mp4";
		
		combineAudio();
		createImageVideo();
		createVideo();

	}

	public void combineAudio() {

		ArrayList<String> audioSelectedList = _creationProcess.getAudioFiles();

		String combineAudioCommand = "sox ";
		for (String audioName  : audioSelectedList)  {
			combineAudioCommand = combineAudioCommand + _filePath  + audioName + ".wav ";

		}
		
		
		combineAudioCommand = combineAudioCommand + _tempAudioFilePath;

		System.out.println(combineAudioCommand);
		try {
			BashCommandClass.runBashProcess(combineAudioCommand);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createImageVideo() {
		ImageDownloader.getImages(_searchTerm, _numImages);
		String lengthOfAudioCommand = "echo `soxi -D " + _tempAudioFilePath + "`";
		String lengthOfAudio = "0.0000"; //IS THIS RIGHT
		try {
			lengthOfAudio = BashCommandClass.getOutputFromCommand(lengthOfAudioCommand);
			System.out.println(lengthOfAudio);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float lengthOfImage =  (float) (1.0000 / (Float.valueOf(lengthOfAudio) / _numImages));
		System.out.println(lengthOfImage);
		
		
		String makeVideoCommand = "ffmpeg -r " + lengthOfImage + " -pattern_type glob -i '" + _filePath + "*.jpg' -c:v libx264 -vf \"scale=-2:min(1080\\,trunc(ih/2)*2)\" "+ _tempVidFilePath 
				+ "&> /dev/null";

		try {
			BashCommandClass.runBashProcess(makeVideoCommand );
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createVideo() { 
		String creationVideoCommand = "ffmpeg -i " + _tempVidFilePath + " -i " + _tempAudioFilePath + " -c:v copy -c:a aac -strict" +
				" experimental " + _creationFilePath + " &> /dev/null; ";
		String deleteTempFileCommand = "rm -r " + _filePath;
		String command = creationVideoCommand + deleteTempFileCommand;

		try {
			BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	

}
