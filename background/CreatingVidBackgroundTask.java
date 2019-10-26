package background;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import application.AudioChunk;
import application.BashCommandClass;
import application.Creation;
import application.Main;
import controllers.CreationProcess;
import javafx.concurrent.Task;

/**
 * Background task for putting together parts of final creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class CreatingVidBackgroundTask extends Task<Boolean>{
	private String _searchTerm;
	private String _fileName;
	private String _filePath;
	private String _tempFilePath;
	private String _slidesFilePath;
	private String _newAudioFilePath;
	private String _audioFilePath;
	private String _tempVidFilePath;
	private String _finalVidFilePath;
	private String _creationFilePath;
	private int _numImages;
	private CreationProcess _creationProcess;
	
	private long audioLength;

	public CreatingVidBackgroundTask() {
		//Setup info fields
		_creationProcess = CreationProcess.getInstance();
		_searchTerm = _creationProcess.getSearchTerm();
		_fileName= "\"" + _creationProcess.getFileName() + "\"";

		//Setup path fields
		_newAudioFilePath = Main._FILEPATH + "/newCreation/audio" + Creation.AUDIO_EXTENTION;
		_audioFilePath = Main._AUDIOPATH + "/" + _fileName + Creation.AUDIO_EXTENTION;
		_slidesFilePath= Main._VIDPATH + "/" + _fileName  + Creation.EXTENTION;
		_tempVidFilePath = Main._FILEPATH + "/newCreation/" + "TempVideo" + Creation.EXTENTION;
		_finalVidFilePath = Main._FILEPATH + "/newCreation/" + "FinalVideo" + Creation.EXTENTION;
		_creationFilePath = Main._CREATIONPATH + "/" + _fileName + Creation.EXTENTION;

		_numImages = _creationProcess.getNumImages();
	}

	@Override
	/**
	 * Execution of task
	 * @return Boolean - Success status of task
	 */
	protected Boolean call() {
		try {
			//Get all selected images
			updateMessage("Combining images");
			getSelectedImages();
			if (isCancelled()) {
				return false;
			}
			updateProgress(20,100);

			//Combine all audio chunks
			updateMessage("Combining audio");
			combineAudio();
			if (isCancelled()) {
				return false;
			}
			updateProgress(30,100);

			//Create video from selected images
			updateMessage("Adding images");
			createImageVideo();
			if (isCancelled()) {
				return false;
			}
			updateProgress(60,100);
			
			//Combine components and add text
			updateMessage("Creating video");
			createVideo();
			if (isCancelled()) {
				return false;
			}
			updateProgress (90,100);
			
			//Add background music
			updateMessage("Adding background music");
			bgMusic();
			if (isCancelled()) {
				return false;
			}
			updateProgress (100,100);
			
			updateMessage("Successfully created");

			//Check size of output video
			String checkLength = "stat -c%s " +  _creationFilePath;
			String stringLength = BashCommandClass.getOutputFromCommand(checkLength);
			int intLength = Integer.parseInt(stringLength);
			//If size is 0, created failed
			if (intLength == 0) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Combine audio chunks
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void combineAudio() throws IOException, InterruptedException {
		List<AudioChunk> audioChunks = _creationProcess.getAudioChunks();
		
		//loop through audio chunks and add to combine command
		String audioChunkPath = Main._FILEPATH + "/newCreation/audioChunks/";
		String combineAudioCommand = "sox ";
		for (AudioChunk chunk  : audioChunks)  {
			combineAudioCommand = combineAudioCommand + audioChunkPath  +  "\"" + chunk.getNum() + "\"" + Creation.AUDIO_EXTENTION + " ";
		}
		
		//Run combine command
		combineAudioCommand = combineAudioCommand + _audioFilePath;
		BashCommandClass.runBashProcess(combineAudioCommand);
	}
	
	/**
	 * Get images selected by user
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void getSelectedImages() throws IOException, InterruptedException {
		//Get file paths for selected images
		ArrayList<String> selectedImagePaths = _creationProcess.getImageList();

		String storeSelectedImageCommand = "";
		int i =0;

		//Move selected images to another directory and resize the images 
		for (String imagePath : selectedImagePaths) {
			storeSelectedImageCommand = storeSelectedImageCommand + "ffmpeg -i " + imagePath + " -vf scale=600:400 " + Main._FILEPATH + "/newCreation/" + i + ".jpg; " ;
			i++;
		}	

		BashCommandClass.runBashProcess(storeSelectedImageCommand);
	}

	/**
	 * Combine images into video
	 * @throws Exception
	 */
	private void createImageVideo() throws Exception {
		File audioFile = new File(Main._AUDIOPATH + "/" + _creationProcess.getFileName() + Creation.AUDIO_EXTENTION);

		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);					
		AudioFormat format = audioInputStream.getFormat();							

		audioLength = audioFile.length();

		int frameSize = format.getFrameSize();
		float audioframeRate = format.getFrameRate();
		float durationInSeconds = (audioLength / (frameSize * audioframeRate));

		//Calculate the image duration and round it to something ffmpeg will tolerate. 3dp accuracy is sufficient for matching the audio length to the slideshow length.
		double frameRate = _numImages/durationInSeconds;
		frameRate = Math.round(frameRate*1000.0)/1000.0;

		String makeVideoCommand = "cat " + Main._FILEPATH + "/newCreation/*.jpg | ffmpeg -f image2pipe -framerate " + frameRate + " -i - -c:v libx264 -vf format=yuv420p -r 25 " + _slidesFilePath ;
		
		BashCommandClass.runBashProcess(makeVideoCommand);
	}

	/**
	 * Combine components
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void createVideo() throws IOException, InterruptedException {
		//Combine video and audio
		String creationVideoCommand = "yes | ffmpeg -i " + _slidesFilePath + " -i " + _audioFilePath + " -c:v copy -c:a aac -strict" + " experimental " + _tempVidFilePath + " &> /dev/null; ";

		//Put text on video
		String textOnVideoCommand = "yes | ffmpeg -i " + _tempVidFilePath + " -vf \"drawtext=fontfile=Montserrat-Regular.ttf:" + "text='" + _searchTerm +"':fontcolor=white:shadowcolor=black:shadowx=4:shadowy=4:fontsize=30:" + "x=(w-text_w)/2:y=(h-text_h)/2\" -codec:a copy " + _finalVidFilePath + "; ";

		//String deleteFileCommand = "rm -r " + _filePath;
		//String command = creationVideoCommand + textOnVideoCommand + deleteFileCommand ;
		String command = creationVideoCommand + textOnVideoCommand ;

		BashCommandClass.runBashProcess(command);
	}

	/**
	 * Add background music
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void bgMusic() throws UnsupportedAudioFileException, IOException, InterruptedException {
		String bgMusic = _creationProcess.getBGMusic();
		
		//Check if user didn't want bg music
		if (bgMusic.equals("None")) {
			String copyVideo = "cp " + _finalVidFilePath + " " + _creationFilePath;
			BashCommandClass.runBashProcess(copyVideo);
			
		} else {
			//Make final video with repeating audio
			String bgMusicPath = Main._RESOURCEPATH + "/" + bgMusic + ".wav";
			String repeatAudio = "ffmpeg -i " + _finalVidFilePath + " -filter_complex \"amovie=" + bgMusicPath + ":loop=0,asetpts=N/SR/TB[aud];[0:a][aud]amix[a]\" -map 0:v -map '[a]' -c:v copy -c:a aac -b:a 256k -shortest " + _creationFilePath;
			BashCommandClass.runBashProcess(repeatAudio);
		}
	}
}
