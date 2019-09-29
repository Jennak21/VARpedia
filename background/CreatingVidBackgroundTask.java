package background;

import java.io.IOException;
import java.util.ArrayList;

import application.BashCommandClass;
import application.Creation;
import application.Main;
import controllers.CreationProcess;
import controllers.ImageDownloader;
import javafx.concurrent.Task;

public class CreatingVidBackgroundTask extends Task<Boolean>{
	private String _searchTerm;
	private String _filePath;
	private String _tempFilePath;
	private String _tempSlidesFilePath;
	private String _tempAudioFilePath;
	private String _tempVidFilePath;
	private String _creationFilePath;
	private int _numImages;
	private CreationProcess _creationProcess;

	public CreatingVidBackgroundTask(String term, int numImages, String filePath, String tempFilePath, String tempAudioFP, String tempSlidesFP, String tempVidFP, String creationFP) {
		_searchTerm = term;
		_numImages = numImages;
		_filePath= filePath;
		_tempFilePath = tempFilePath;
		_tempSlidesFilePath = tempSlidesFP;
		_tempAudioFilePath = tempAudioFP;
		_tempVidFilePath = tempVidFP;
		_creationFilePath = creationFP; 
		_creationProcess = CreationProcess.getInstance();
	}

	@Override
	protected Boolean call() {
		try {
			makeTempDir();
			combineAudio();
			createImageVideo();
			createVideo();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void makeTempDir() throws IOException, InterruptedException {
		
		updateMessage("Starting Creation");
		
		//make directory for vid files
		String makeVidDirCommand = "mkdir " + Main._FILEPATH  + "/" + "newCreation" + "/" + "vidCreationTemp";

		BashCommandClass.runBashProcess(makeVidDirCommand);
		
		updateProgress (10,100);

	}

	public void createImageVideo() throws Exception {


		updateMessage("Fetching Images");

		ImageDownloader.getImages(_searchTerm, _numImages);
		
		updateProgress(50,100);
		
		updateMessage("Compiling images");
		
		//run bash command that gets length of audio
		String lengthOfAudioCommand = "echo `soxi -D " + _tempAudioFilePath + "`";
		String lengthOfAudio = "0.00"; 
		lengthOfAudio = BashCommandClass.getOutputFromCommand(lengthOfAudioCommand);

		//calculate duration to use in bash command that will create a slideshow
		double lengthOfImage =  (1.00 / (Double.valueOf(lengthOfAudio) / _numImages));


		String makeVideoCommand = "ffmpeg -r " + lengthOfImage  + " -pattern_type glob -i '" + _tempFilePath + "*.jpg' -c:v libx264 -vf \"scale=-2:min(1080\\,trunc(ih/2)*2)\" "+ _tempSlidesFilePath 
				+ "&> /dev/null";

		BashCommandClass.runBashProcess(makeVideoCommand );
		

		updateProgress(70,100);


	}

	private void createVideo() throws IOException, InterruptedException {
		updateMessage("Creating video");
		
		//combine video and audio
		String creationVideoCommand = "ffmpeg -i " + _tempSlidesFilePath + " -i " + _tempAudioFilePath + " -c:v copy -c:a aac -strict" +
				" experimental " + _tempVidFilePath + " &> /dev/null; ";

		updateProgress(80,100);

		//put text on video
		String textOnVideoCommand = "ffmpeg -i " + _tempVidFilePath + " -vf \"drawtext=fontfile=Montserrat-Regular.ttf:" + 
				"text='" + _searchTerm +"':fontcolor=white:fontsize=24:" + 
				"x=(w-text_w)/2:y=(h-text_h)/2\" -codec:a copy " +_creationFilePath + "; ";

		String deleteFileCommand = "rm -r " + _filePath;
		String command = creationVideoCommand + textOnVideoCommand + deleteFileCommand ;

		BashCommandClass.runBashProcess(command);
		
		//update user
		updateMessage("Successfully created");
		updateProgress (100,100);
		
		

	}

	public void combineAudio() throws IOException, InterruptedException {

		updateMessage("Combining Audio");

		ArrayList<String> audioSelectedList = _creationProcess.getAudioFiles();

		//loop through audio on the selectedlist and add to bash command which will return length of audio
		String combineAudioCommand = "sox ";
		for (String audioName  : audioSelectedList)  {
			combineAudioCommand = combineAudioCommand + _filePath  +  "\"" + audioName + "\"" + Creation.AUDIO_EXTENTION + " ";

		}

		combineAudioCommand = combineAudioCommand + _tempAudioFilePath;
	

		BashCommandClass.runBashProcess(combineAudioCommand);
		
		//update user
		updateProgress (30,100);

	}

	
}
