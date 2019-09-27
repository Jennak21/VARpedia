package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.BashCommandClass;
import application.Creation;
import application.Main;
import javafx.fxml.Initializable;


public class CreatingVideoContoller extends SceneChanger implements Initializable{ 

	private CreationProcess _creationProcess;
	private String _searchTerm;
	private int _numImages;
	private String _fileName;
	private String _filePath = Main._FILEPATH +"/newCreation/";
	private String _tempAudioFilePath;
	private String _tempSlidesFilePath;
	private String _tempVidFilePath;
	private String _creationFilePath;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_creationProcess = CreationProcess.getInstance();
		_searchTerm = _creationProcess.getSearchTerm();
		_numImages = _creationProcess.getNumImages();
		_fileName= "\"" + _creationProcess.getFileName() + "\"";
	
		// TODO Auto-generated method stub
		
		_tempAudioFilePath = _filePath + _fileName + "TempAudio" + Creation.AUDIO_EXTENTION ;
		_tempSlidesFilePath= _filePath + _fileName + "TempSlide" + Creation.EXTENTION;
		_tempVidFilePath = _filePath  + _fileName + "TempVideo" + Creation.EXTENTION;
		_creationFilePath = Main._FILEPATH + "/" + _fileName + Creation.EXTENTION;
		
		combineAudio();
		createImageVideo();
		createVideo();

	}

	public void combineAudio() {

		ArrayList<String> audioSelectedList = _creationProcess.getAudioFiles();

		String combineAudioCommand = "sox ";
		for (String audioName  : audioSelectedList)  {
			combineAudioCommand = combineAudioCommand + _filePath  +  "\"" + audioName + "\"" + Creation.AUDIO_EXTENTION + " ";

		}
		
		
		combineAudioCommand = combineAudioCommand + _tempAudioFilePath;
		System.out.println(combineAudioCommand);

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
		String lengthOfAudio = "0.00"; //IS THIS RIGHT
		try {
			lengthOfAudio = BashCommandClass.getOutputFromCommand(lengthOfAudioCommand);
			System.out.println("hi" + lengthOfAudio);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double lengthOfImage =  (1.00 / (Double.valueOf(lengthOfAudio) / _numImages));
		System.out.println(lengthOfImage);
		
		
		String makeVideoCommand = "ffmpeg -r " + lengthOfImage  + " -pattern_type glob -i '" + _filePath + "*.jpg' -c:v libx264 -vf \"scale=-2:min(1080\\,trunc(ih/2)*2)\" "+ _tempSlidesFilePath 
				+ "&> /dev/null";

		try {
			BashCommandClass.runBashProcess(makeVideoCommand );
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createVideo() { 
		String creationVideoCommand = "ffmpeg -i " + _tempSlidesFilePath + " -i " + _tempAudioFilePath + " -c:v copy -c:a aac -strict" +
				" experimental " + _tempVidFilePath + " &> /dev/null; ";
		System.out.println(creationVideoCommand);
		String textOnVideoCommand = "ffmpeg -i " + _tempVidFilePath + " -vf \"drawtext=fontfile=Montserrat-Regular.ttf:" + 
				"text='" + _searchTerm +"':fontcolor=white:fontsize=24:box=1: boxcolor=black@0.5:" + 
				"boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2\" -codec:a copy " +_creationFilePath ;
		
		System.out.println(textOnVideoCommand);
//		String deleteTempFileCommand = "rm -r " + _filePath;
		String command = creationVideoCommand + textOnVideoCommand ;

		try {
			BashCommandClass.runBashProcess(command);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	

}
