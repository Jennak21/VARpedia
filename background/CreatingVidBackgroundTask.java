package background;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import application.BashCommandClass;
import application.Creation;
import application.Main;
import controllers.CreationProcess;
import controllers.ImageDownloader;
import javafx.concurrent.Task;

public class CreatingVidBackgroundTask extends Task<Boolean>{
	private String _searchTerm;
	private String _fileName;
	private String _filePath;
	private String _tempFilePath;
	private String _slidesFilePath;
	private String _newAudioFilePath;
	private String _audioFilePath;
	private String _tempVidFilePath;
	private String _creationFilePath;
	private int _numImages;
	private CreationProcess _creationProcess;
	
	public CreatingVidBackgroundTask() {
		
		_creationProcess = CreationProcess.getInstance();
		_searchTerm = _creationProcess.getSearchTerm();
		_numImages = _creationProcess.getNumImages();
		_fileName= "\"" + _creationProcess.getFileName() + "\"";

		//setup paths
		_newAudioFilePath = Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
		_audioFilePath = Main._AUDIOPATH + "/" + _fileName + Creation.AUDIO_EXTENTION;
		_slidesFilePath= Main._VIDPATH + "/" + _fileName  + Creation.EXTENTION;
		_tempVidFilePath = Main._FILEPATH + "/newCreation/" + "TempVideo" + Creation.EXTENTION;
		_creationFilePath = Main._CREATIONPATH + "/" + _fileName + Creation.EXTENTION;
	}

	@Override
	protected Boolean call() {
		try {
			copyAudio();
			if (isCancelled()) {
				return false;
			}
			makeTempDir();
			if (isCancelled()) {
				return false;
			}
			
			combineAudio();
			if (isCancelled()) {
				return false;
			}
			
			createImageVideo();
			if (isCancelled()) {
				return false;
			}
			
			createVideo();
			if (isCancelled()) {
				return false;
			}
			
			String checkLength = "stat -c%s " +  _creationFilePath;
			String stringLength = BashCommandClass.getOutputFromCommand(checkLength);
			int intLength = Integer.parseInt(stringLength);
			if (intLength == 0) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void copyAudio() throws IOException, InterruptedException {
		String command = "cp " + Main._FILEPATH + "/audio" + Creation.AUDIO_EXTENTION + " " +_audioFilePath;
		BashCommandClass.runBashProcess(command);
	}

	private void makeTempDir() throws IOException, InterruptedException {
		
		updateMessage("Starting Creation");
		
		//make directory for vid files
		String makeVidDirCommand = "mkdir " + Main._FILEPATH  + "/" + "newCreation" + "/" + "vidCreationTemp";

		BashCommandClass.runBashProcess(makeVidDirCommand);
		
		updateProgress (10,100);

	}

	private void createImageVideo() throws Exception {


		updateMessage("Fetching Images");

		ImageDownloader.getImages(_searchTerm, _numImages);
		
		updateProgress(50,100);
		
		updateMessage("Compiling images");
		
//		//run bash command that gets length of audio
//		String lengthOfAudioCommand = "echo `soxi -D " + _tempAudioFilePath + "`";
//		
//		String lengthOfAudio = BashCommandClass.getOutputFromCommand(lengthOfAudioCommand);
//
//		//calculate duration to use in bash command that will create a slideshow
//		float lengthOfImage = Float.valueOf(1)/ (Float.valueOf(lengthOfAudio) / _numImages);
		
		
		
		File audioFile = new File(_tempFilePath + _creationProcess.getFileName() + "TempAudio"  + Creation.AUDIO_EXTENTION);
		
		
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);					
		AudioFormat format = audioInputStream.getFormat();							
		
		long audioFileLength = audioFile.length();
		
		
		
		int frameSize = format.getFrameSize();
		
		float audioframeRate = format.getFrameRate();
		
		float durationInSeconds = (audioFileLength / (frameSize * audioframeRate));
		
		
		

		//calculate the image duration and round it to something ffmpeg will tolerate. 3dp accuracy is sufficient for matching the audio length to the slideshow length.
		double frameRate = _numImages/durationInSeconds;
		frameRate = Math.round(frameRate*1000.0)/1000.0;

		System.out.println(frameRate);
		String makeVideoCommand = "ffmpeg -framerate " + frameRate + " -i \"" + Main._FILEPATH + "/newCreation/%01d.jpg\" -c:v libx264 -vf \"scale=-2:min(1080\\,trunc(ih/2)*2)\" -r 25 "+ _slidesFilePath 
				+ "&> /dev/null";
		
		//String makeVideoCommand =  " ffmpeg -y -framerate " + frameRate + " -i  " + _tempFilePath + "%01d.jpg -c:v libx264 -vf \"scale=-2:min(1080\\,trunc(ih/2)*2)\" -r 25 " + _tempSlidesFilePath + " > /dev/null";
		
		System.out.println(makeVideoCommand);
	
		BashCommandClass.runBashProcess(makeVideoCommand);
		
		

		updateProgress(70,100);


	}

	private void createVideo() throws IOException, InterruptedException {
		updateMessage("Creating video");
		
		//combine video and audio
		String creationVideoCommand = "ffmpeg -i " + _slidesFilePath + " -i " + _audioFilePath + " -c:v copy -c:a aac -strict" +
				" experimental " + _tempVidFilePath + " &> /dev/null; ";

		updateProgress(80,100);

		//put text on video
		String textOnVideoCommand = "ffmpeg -i " + _tempVidFilePath + " -vf \"drawtext=fontfile=Montserrat-Regular.ttf:" + 
				"text='" + _searchTerm +"':fontcolor=white:fontsize=24:" + 
				"x=(w-text_w)/2:y=(h-text_h)/2\" -codec:a copy " +_creationFilePath + "; ";

		String deleteFileCommand = "rm -r " + _filePath;
//		String command = creationVideoCommand + textOnVideoCommand + deleteFileCommand ;
		String command = creationVideoCommand + textOnVideoCommand ;

		BashCommandClass.runBashProcess(command);
		
		//update user
		updateMessage("Successfully created");
		updateProgress (100,100);
		
		

	}

	private void combineAudio() throws IOException, InterruptedException {

		updateMessage("Combining Audio");

		ArrayList<String> audioSelectedList = _creationProcess.getAudioFiles();

		//loop through audio on the selectedlist and add to bash command which will return length of audio
		String combineAudioCommand = "sox ";
		for (String audioName  : audioSelectedList)  {
			combineAudioCommand = combineAudioCommand + _filePath  +  "\"" + audioName + "\"" + Creation.AUDIO_EXTENTION + " ";

		}

		combineAudioCommand = combineAudioCommand + _audioFilePath;
	

		BashCommandClass.runBashProcess(combineAudioCommand);
		
		//update user
		updateProgress (30,100);

	}

	
}
