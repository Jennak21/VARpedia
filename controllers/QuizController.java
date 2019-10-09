package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.BashCommandClass;
import application.Creation;
import application.ErrorAlert;
import application.Main;
import background.QuizResultsBackgroundTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class QuizController extends SceneChanger {
	@FXML
	private GridPane _quizPane;
	@FXML
	private Label _progressLabel;
	@FXML
	private Button _quitButton;
	
	//Settings pane
	@FXML
	private GridPane _optionsGrid;
	@FXML
	private Button _startButton;
	@FXML
	private Slider _questionsSlider;
	
	
	//Quiz pane
	@FXML
	private GridPane _quizGrid;
	@FXML
	private Button _submitButton;
	@FXML
	private TextField _guessField;
	@FXML
	private Label _guessLabelTop;
	@FXML
	private Label _guessLabel;
	@FXML
	private Label _correctLabelTop;
	@FXML
	private Label _correctLabel;
	@FXML
	private StackPane _videoStackPane;
	@FXML
	private BorderPane _videoControlsPane;
	@FXML
	private HBox _videoControlsBar;
	@FXML
	private StackPane _audioStackPane;
	@FXML
	private BorderPane _audioControlsPane;
	@FXML
	private VBox _audioControlsBar;
	
	//Result pane
	@FXML
	private GridPane _summaryGrid;
	@FXML
	private Label _resultsTotalLabel;
	@FXML
	private ListView _resultsList;
	
	
	private VideoPlayer _videoPlayer;
	private AudioPlayer _audioPlayer;
	private int _numQuestions;
	private int _currentQuestion;
	private int _numCorrect;
	private Creation _creation;
	private List<QuizResult> _quizResults;
	
	@FXML
	private void initialize() {
		//Ensure correct components are visible
		_submitButton.setDisable(true);
		
		_optionsGrid.setVisible(true);
		_quizGrid.setVisible(false);
		_summaryGrid.setVisible(false);
		
		_quizResults = new ArrayList<QuizResult>();
	
		//Initially set quit button to go back to main menu
		_quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				finishHandle();
			}
		});
	}
	
	@FXML
	private void startHandle() {
		//Setup fields
		_numQuestions = (int)_questionsSlider.getValue();
		_currentQuestion = 0;
		_numCorrect = 0;
		
		//Load correct components
		_optionsGrid.setVisible(false);
		_quizGrid.setVisible(true);
		_guessLabel.setVisible(false);
		_guessLabelTop.setVisible(false);
		_correctLabel.setVisible(false);
		_correctLabelTop.setVisible(false);
		_guessField.setVisible(true);
		
		_submitButton.setDisable(false);
		
		//Change quit button action to load results
		_quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				quitHandle();
			}
		});
		
		//Create video player with custom control set
		_videoPlayer = new VideoPlayer(_videoStackPane, _videoControlsPane, _videoControlsBar);
		_videoPlayer.removeVolControls();
		_videoPlayer.removeTimeLabels();
		
		_audioPlayer = new AudioPlayer(_audioStackPane, _audioControlsPane, _audioControlsBar);
		
		
		//Get first question
		loadQuestion();
	}
	
	private void loadQuestion() {
		_currentQuestion++;
		//Reset guess field
		_guessField.clear();
		
		//Check if quiz is finished (desired number of questions have been answered)
		if (_currentQuestion > _numQuestions) {
			//All questions answered
			finishQuiz();
		} else {
			//More questions - update progress and load next question
			_progressLabel.setText(_currentQuestion + " of " + _numQuestions);

			randomCreation();
			
			String videoPath = Main._CREATIONPATH + "/apple" + Creation.EXTENTION;
			_videoPlayer.setMedia(videoPath);
			
			String audioPath = Main._AUDIOPATH + "/audio" + Creation.AUDIO_EXTENTION;
			_audioPlayer.setMedia(audioPath);
		}
		
		_guessField.requestFocus();
	}
	
	private void randomCreation() {
		//Setup
		int accuracySum = 0;
		List<Creation> creationList = Main.getCreationList();
		
		//Go through all creations and add up accuracy scores
		for (Creation c : creationList) {
			accuracySum += c.getAccuracyWeighting();
		}
		
		//Generate random num
		double randomAccuracyNum = Math.random()*accuracySum;
		//Index tracking
		int creationIndex = -1;
		
		//Subtract from random num until it drops below zero
		while (randomAccuracyNum > 0) {
			creationIndex++;
			randomAccuracyNum -= creationList.get(creationIndex).getAccuracyWeighting();
		}
		
		//Set next quiz question to the resulting creation
		_creation = creationList.get(creationIndex);
	}
	
	@FXML
	private void submitHandle() {
		//Check whether user is coming from guessing pane or result pane
		if (_submitButton.getText().equals("Submit")) {	//Coming from guessing pane
			
			//Display result labels with relevant info
			_guessLabelTop.setVisible(true);
			_guessLabel.setVisible(true);
			_correctLabelTop.setVisible(true);
			_correctLabel.setVisible(true);
			_guessField.setVisible(false);
			
			String guessTerm = _guessField.getText().toLowerCase().trim();
			String correctTerm = _creation.getSearchTerm().toLowerCase().trim();
			boolean correctAnswer = guessTerm.equals(correctTerm);
			
			_guessLabel.setText(guessTerm);
			_correctLabel.setText(correctTerm);
			
			//Find whether current creation has been tested before
			QuizResult currentResult = null;
			for (QuizResult q: _quizResults) {
				if (q.sameCreation(_creation)) {
					currentResult = q;
				}
			}
			//If current creation hasn't been tested before
			if (currentResult == null) {
				//Create new result instance and add to list
				currentResult = new QuizResult(_creation);
				_quizResults.add(currentResult);
			}
			
			//Add result to result instance
			currentResult.addResult(correctAnswer);
			
			//If correct answer, increment relevant var
			if (guessTerm.equals(correctTerm)) {
				_numCorrect++;
			}
			
			//Set button text based on whether there is another question or not
			if (_currentQuestion != _numQuestions) {
				_submitButton.setText("Next");
			} else {
				_submitButton.setText("Finish");
			}
		} else {	//Coming from result pane
			_guessLabelTop.setVisible(false);
			_guessLabel.setVisible(false);
			_correctLabelTop.setVisible(false);
			_correctLabel.setVisible(false);
			_guessField.setVisible(true);
			_submitButton.setText("Submit");
			
			loadQuestion();
		}
	}
	
	@FXML
	private void quitHandle() {
		if (_videoPlayer != null) {
			_videoPlayer.stopVideo();
		}
		
		finishQuiz();
	}
	
	private void finishQuiz() {
		//Load results pane - disable other components
		_quitButton.setVisible(false);
		_submitButton.setVisible(false);
		_quizGrid.setVisible(false);
		_summaryGrid.setVisible(true);
		
		_progressLabel.setText("Results");
		_resultsTotalLabel.setText(_numCorrect + "/" + _numQuestions);
		
		//Load results into list
		loadResults();
		
		//Save result statistics
		saveResults();
	}
	
	private void loadResults() {
		//String list for results
		ObservableList results = FXCollections.observableArrayList();
		
		//Get results for all creations that were tested
		for (QuizResult q: _quizResults) {
			//Store result in results list
			results.add(q.getResultString());
		}
		
		//Set list
		_resultsList.setItems(results);
	}
	
	private void saveResults() {
		QuizResultsBackgroundTask quizResultsTask = new QuizResultsBackgroundTask(_quizResults);
		Thread resultsThread = new Thread(quizResultsTask);
		resultsThread.start();
	}
	
	@FXML
	private void finishHandle() {
		try {
			changeScene(_quizPane, "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't load main menu");
		}
	}
}
