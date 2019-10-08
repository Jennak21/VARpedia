package controllers;

import java.io.IOException;
import java.util.List;

import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class QuizController extends SceneChanger {
	@FXML
	private GridPane _quizPane;
	@FXML
	private Button _submitButton;
	@FXML
	private Button _quitButton;
	@FXML
	private Label _progressLabel;
	@FXML
	private GridPane _optionsGrid;
	@FXML
	private GridPane _quizGrid;
	@FXML
	private Button _startButton;
	@FXML
	private Slider _questionsSlider;
	@FXML
	private GridPane _summaryGrid;
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
	private Label _resultsTotalLabel;
	
	private int _numQuestions;
	private int _currentQuestion;
	private int _numCorrect;
	private Creation _creation;
	
	@FXML
	private void initialize() {
		_quitButton.setDisable(true);
		_submitButton.setDisable(true);
		
		_optionsGrid.setVisible(true);
		_quizGrid.setVisible(false);
		_summaryGrid.setVisible(false);
	}
	
	@FXML
	private void startHandle() {
		_numQuestions = (int)_questionsSlider.getValue();
		_currentQuestion = 0;
		_numCorrect = 0;
		
		_optionsGrid.setVisible(false);
		_quizGrid.setVisible(true);
		_guessLabel.setVisible(false);
		_guessLabelTop.setVisible(false);
		_correctLabel.setVisible(false);
		_correctLabelTop.setVisible(false);
		_guessField.setVisible(true);
		
		_quitButton.setDisable(false);
		_submitButton.setDisable(false);
		
		loadQuestion();
	}
	
	private void loadQuestion() {
		_currentQuestion++;
		_guessField.clear();
		
		if (_currentQuestion > _numQuestions) {
			finishQuiz();
		} else {
			_progressLabel.setText(_currentQuestion + " of " + _numQuestions);
			
			randomCreation();
			
			System.out.println(_creation.getSearchTerm());
		}
	}
	
	private void randomCreation() {
		int accuracySum = 0;
		List<Creation> creationList = Main.getCreationList();
		
		for (Creation c : creationList) {
			String creationAcc = c.getAccuracy();
			accuracySum += 150 - Integer.parseInt(creationAcc);
		}
		
		double randomAccuracyNum = Math.random()*accuracySum;
		int creationIndex = -1;
		while (randomAccuracyNum > 0) {
			creationIndex++;
			String creationAccuracy = creationList.get(creationIndex).getAccuracy();
			randomAccuracyNum -= 150 - Integer.parseInt(creationAccuracy);
		}
		
		_creation = creationList.get(creationIndex);
	}
	
	@FXML
	private void submitHandle() {
		if (_submitButton.getText().equals("Submit")) {
			_guessLabelTop.setVisible(true);
			_guessLabel.setVisible(true);
			_correctLabelTop.setVisible(true);
			_correctLabel.setVisible(true);
			_guessField.setVisible(false);
			_submitButton.setText("Next");
			
			String guessTerm = _guessField.getText().toLowerCase().trim();
			String correctTerm = _creation.getSearchTerm().toLowerCase().trim();
			
			_guessLabel.setText(guessTerm);
			_correctLabel.setText(correctTerm);
			
			if (guessTerm.equals(correctTerm)) {
				_numCorrect++;
			} else {
				
			}	
		} else {
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
		finishQuiz();
	}
	
	private void finishQuiz() {
		_quitButton.setVisible(false);
		_submitButton.setVisible(false);
		_quizGrid.setVisible(false);
		_summaryGrid.setVisible(true);
		
		_progressLabel.setText("Results");
		_resultsTotalLabel.setText(_numCorrect + "/" + _numQuestions);
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
