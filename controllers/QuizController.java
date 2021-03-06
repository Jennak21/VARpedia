package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

import application.Creation;
import application.Main;
import application.QuizResult;
import background.QuizResultsBackgroundTask;
import background.TranslatorBackgroundTask;
import customUI.AudioPlayer;
import customUI.ErrorAlert;
import customUI.VideoPlayer;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for quiz
 * @author Max Gurr & Jenna Kumar
 *
 */
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
	@FXML
	private ComboBox<Language> _languageChoicebox;
	@FXML
	private Label _connectingLabel;
	@FXML
	private Label _languageLabel;
	
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
	private StackPane _answerPane;
	@FXML
	private GridPane _summaryGrid;
	@FXML
	private Label _resultsTotalLabel;
	@FXML
	private TableView<QuizResult> _resultsTable;
	@FXML
	private Button _helpButton;	
	@FXML
	private StackPane _helpPane;
	@FXML
	private TextArea _helpText;
	@FXML
	private Button _finishButton;

	private VideoPlayer _videoPlayer;
	private AudioPlayer _audioPlayer;
	private int _numQuestions;
	private int _currentQuestion;
	private int _numCorrect;
	private Creation _creation;
	private List<QuizResult> _quizResults;
	private Language _language;
	private String _correctTerm;
	
	/**
	 * Enum to represent translation languages
	 * @author student
	 *
	 */
	private enum Language {
		ENGLISH("English", "en"),
		AFRIKAANS("Afrikaans", "af"),
		ARABIC("Arabic", "ar"),
		BULGARIAN("Bulgarian", "bg"),
		CHINESE("Chinese (simplified)", "zh-CN"),
		DUTCH("Dutch", "nl"),
		FRENCH("French", "fr"),
		HINDI("Hindi", "hi"),
		ITALIAN("Italian", "it"),
		JAPANESE("Japanese", "ja"),
		MAORI("Maori", "mi"),
		RUSSIAN("Russian", "ru"),
		SPANISH("Spanish", "es"),
		TAMIL("Tamil", "ta"),
		URDU("Urdu", "ur"),
		VIETNAMESE("Vietnamese", "vi");
		
		private final String _name;
		private final String _code;
		
		private Language(String name, String code) {
			this._name = name;
			this._code = code;
		}
		
		private String getCode() {
			return _code;
		}
		
		@Override
		public String toString() {
			return this._name;
		}
	}
	
	@FXML
	/**
	 * Run when scene loads
	 */
	private void initialize() {
		loadSettingsScene();

		_quizResults = new ArrayList<QuizResult>();
		
		//Load languages into choice box
		ObservableList<Language> languageList = FXCollections.observableArrayList(Arrays.asList(Language.values()));
		_languageChoicebox.setItems(languageList);
		_languageChoicebox.getSelectionModel().selectFirst();

		
		//Initially set quit button to go back to main menu
		_quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				finishHandle();
			}
		});
	}
	
	/**
	 * Loads all settings components and hides other components
	 */
	private void loadSettingsScene() {
		//set help components as not visible
		_helpPane.setVisible(false);
		_helpText.setVisible(false);

		//Ensure correct components are visible
		_submitButton.setDisable(true);
		_optionsGrid.setVisible(true);
		_answerPane.setVisible(false);
		_quizGrid.setVisible(false);
		_summaryGrid.setVisible(false);
		_submitButton.setVisible(false);
		_connectingLabel.setVisible(false);
		_startButton.setVisible(true);
		_languageLabel.setVisible(false);
	}

	@FXML
	/**
	 * Start quiz - Get language and try connect to server
	 */
	private void startHandle() {
		//Set selected language
		_language = _languageChoicebox.getSelectionModel().getSelectedItem();
		
		//If not english, test translation connection
		if (_language != Language.ENGLISH) {
			TranslatorBackgroundTask translationTask = new TranslatorBackgroundTask(_language.getCode(), "Hello");
			Thread translationThread = new Thread(translationTask);
			translationThread.start();
			
			//Notify user of connection 
			translationTask.setOnRunning(start -> {
				_startButton.setVisible(false);
				_connectingLabel.setVisible(true);;
			});
			
			//Get result of connection test, act accordingly
			translationTask.setOnSucceeded(finish -> {
				if (translationTask.getValue()) {
					startQuiz();
				} else {
					new ErrorAlert("Couldn't connect to server");
					loadSettingsScene();
				}
			});
		} else {
			//User selected english, no need to translate
			startQuiz();
		}
	}
	
	/**
	 * Start quiz - Initialise quiz values
	 */
	private void startQuiz() {
		//Set language label to chosen language
		_languageLabel.setText("Language: " + _language);
		_languageLabel.setVisible(true);
		
		//make submit button visible
		_submitButton.setVisible(true);

		//Setup fields
		_numQuestions = (int)_questionsSlider.getValue();
		_currentQuestion = 0;
		_numCorrect = 0;

		//Load correct components
		_optionsGrid.setVisible(false);
		_quizGrid.setVisible(true);
		//set rows showing guess and correct answers to not visible
		_answerPane.setVisible(false);
		
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

	/**
	 * Load new question to quiz
	 */
	private void loadQuestion() {
		_currentQuestion++;
		//Reset guess field
		_guessField.clear();

		//Check if quiz is finished (desired number of questions have been answered)
		if (_currentQuestion > _numQuestions) {
			//All questions answered
			finishQuiz();
		} else {
			//More questions - update progress
			_progressLabel.setText("Question " + _currentQuestion + " of " + _numQuestions);
			
			//Get random new creation
			randomCreation();
			
			//Load video and audio
			String filename = _creation.getFilename();
			String videoPath = Main._VIDPATH + "/" + filename + Creation.EXTENTION;
			_videoPlayer.setMedia(videoPath);
			String audioPath = Main._AUDIOPATH + "/" + filename + Creation.AUDIO_EXTENTION;
			_audioPlayer.setMedia(audioPath);
			
			//If desired language is not english, translate creation term
			if (_language != Language.ENGLISH) {
				//Find out whether question has been presented before (so translation already done)
				boolean existingQuizResult = false;
				for (QuizResult q: _quizResults) {
					if (q.sameFileCreation(_creation.getFilename())) {
						//Previous question found for same creation, get translated term
						_correctTerm = q.getTerm();
						existingQuizResult = true;
					}
				}
				
				//If new question hasn't been asked before, get the translated term
				if (!existingQuizResult) {
					TranslatorBackgroundTask translationTask = new TranslatorBackgroundTask(_language.getCode(), _creation.getSearchTerm());
					Thread translationThread = new Thread(translationTask);
					translationThread.start();
					
					//Disable 'submit' button while getting translation
					translationTask.setOnRunning(start -> {
						_submitButton.setText("Translating");
						_submitButton.setDisable(true);
					});
					
					translationTask.setOnSucceeded(finish -> {
						//Set correct guess term
						if (translationTask.getValue()) {
							//Get result of translation and convert html text codes
							String translationResult = Jsoup.parse(translationTask.getMessage()).text().toLowerCase().trim();
	
							_correctTerm = translationResult;
						} else {
							_correctTerm = _creation.getSearchTerm().toLowerCase().trim();
						}
						
						//Enable submit button
						_submitButton.setText("Submit");
						_submitButton.setDisable(false);
					});	
				}
			} else {
				//Language is english, use english search term
				_correctTerm = _creation.getSearchTerm().toLowerCase().trim();
			}
		}

		_guessField.requestFocus();
	}

	/**
	 * Get a random creation
	 */
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
	/**
	 * Run when user types
	 */
	private void onTypeHandler(KeyEvent event) {
		//if key pressed is enter and submit button is enabled, submit answer
		if (event.getCode() == KeyCode.ENTER && !_submitButton.isDisable()) {
			submitAnswer();
		}
	}

	/**
	 * Process submitted answer
	 */
	private void submitAnswer() {
		//Display result labels with relevant info
		_guessField.setVisible(false);
		_answerPane.setVisible(true);
	
		//Funky for answer labels
		FadeTransition ft = new FadeTransition(Duration.millis(1000), _answerPane);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();

		//Find whether current creation has been tested before
		QuizResult currentResult = null;
		for (QuizResult q: _quizResults) {
			if (q.sameFileCreation(_creation.getFilename())) {
				currentResult = q;
			}
		}
		//If current creation hasn't been tested before
		if (currentResult == null) {
			//Create new result object and add to list
			currentResult = new QuizResult(_creation);
			_quizResults.add(currentResult);
			
			currentResult.setTerm(_correctTerm);
		}
		
		String guessTerm = _guessField.getText().toLowerCase().trim();
		boolean correctAnswer = guessTerm.equals(_correctTerm);

		_guessLabel.setText(guessTerm);
		_correctLabel.setText(_correctTerm);

		//Add result to result instance
		currentResult.addResult(correctAnswer);

		//If correct answer, increment correct total
		if (guessTerm.equals(_correctTerm)) {
			_numCorrect++;
		}

		//Set button text based on whether there is another question or not
		if (_currentQuestion != _numQuestions) {
			_submitButton.setText("Next");
		} else {
			_submitButton.setText("Finish");
		}
		
		//set mouse focus to the submit/nextButton
		_submitButton.requestFocus();
	}

	@FXML
	/**
	 * Submit button - Display correct answer / Load next question
	 */
	private void submitHandle() {
		//Check whether user is coming from guessing pane or result pane
		if (_submitButton.getText().equals("Submit")) {	
			//Coming from guessing pane
			submitAnswer();
		} 
		else {	
			//Coming from answer pane
			_guessField.setVisible(true);
			_submitButton.setText("Submit");
			//Set answer pane to not visible
			_answerPane.setVisible(false);

			loadQuestion();
		}
	}

	@FXML
	/**
	 * User wants to quiz, stop media and load quiz results
	 */
	private void quitHandle() {
		//Stop media
		if (_videoPlayer != null) {
			_videoPlayer.stopVideo();
		}
		if (_audioPlayer != null) {
			_audioPlayer.stopAudio();
		}

		finishQuiz();
	}

	/**
	 * Finish questions - load result pane
	 */
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

	/**
	 * Load quiz results into table
	 */
	private void loadResults() {
		//Get results for all creations that were tested
		for (QuizResult q: _quizResults) {
			//Calculate change in learning %
			q.calculateDeltaTestAcc();
		}
				
		//Set tableview data to list of creation objects
		ObservableList<QuizResult> resultsData = FXCollections.observableList(_quizResults);
		
		resultsData = FXCollections.observableList(resultsData);
		_resultsTable.setItems(resultsData);
        
        TableColumn<QuizResult, String> nameCol = new TableColumn<>("Creation");
        nameCol.setCellValueFactory(new PropertyValueFactory<QuizResult, String>("creationName"));
        nameCol.setSortable(false);
        TableColumn<QuizResult, String> termCol = new TableColumn<>("Term");
        termCol.setCellValueFactory(new PropertyValueFactory<QuizResult, String>("term"));
        termCol.setSortable(false);
        TableColumn<QuizResult, String> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<QuizResult, String>("resultString"));
        scoreCol.setSortable(false);
        TableColumn<QuizResult, String> learningCol = new TableColumn<>("Learning %");
        learningCol.setCellValueFactory(new PropertyValueFactory<QuizResult, String>("learning"));
        learningCol.setSortable(false);
        
        _resultsTable.getColumns().setAll(nameCol, termCol, scoreCol, learningCol);
        _resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        _resultsTable.getSelectionModel().selectFirst();
        

		//set mouse focus to the finish button
		_finishButton.requestFocus();
	}
	
	/**
	 * Save quiz results to info file
	 */
	private void saveResults() {
		QuizResultsBackgroundTask quizResultsTask = new QuizResultsBackgroundTask(_quizResults);
		Thread resultsThread = new Thread(quizResultsTask);
		resultsThread.start();
	}

	@FXML
	/**
	 * Go back to main menu
	 */
	private void finishHandle() {
		try {
			changeScene(_quizPane, "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't load main menu");
		}
	}

	@FXML
	/**
	 * Help button handle
	 */
	private void helpHandle(ActionEvent event) {
		_helpPane.setVisible(true);
		_helpText.setVisible(true);
		_helpButton.setVisible(false);

	}

	@FXML
	/**
	 * Exit help
	 */
	private void exitHelpHandle(ActionEvent event) {
		_helpPane.setVisible(false);
		_helpText.setVisible(false);
		_helpButton.setVisible(true);

	}
}
