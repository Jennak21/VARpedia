package controllers;

import java.io.IOException;

import application.Creation;
import application.ErrorAlert;
import application.Main;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MediaScreenController extends SceneChanger {
	@FXML
	private StackPane _mediaStackPane;
	@FXML
	private BorderPane _controlsPane;
	@FXML
	private HBox _bottomControls;
	@FXML
	private Button _button;
	@FXML
	private Button _backButton;

	private MediaView _view;
	private MediaPlayer _player;
	private Button _playPauseButton;
	private Slider _timeSlider;
	private Label _playTime;
	private Slider _volSlider;
	private Duration _duration;
    private boolean _atEndOfMedia = false;
    	
	@FXML
	private void initialize() {
		//Setup player and load video
		CreationStore process = CreationStore.getInstance();
		Creation creation = process.getCreation();
		String creationName = creation.getName();
		String fullFilePath = Main._FILEPATH + "/" + creationName + Creation.getExtention();
		Media media = new Media("file://" + fullFilePath);
		_player = new MediaPlayer(media);
		_view = new MediaView(_player);
		
		
		//Play/pause button
		//Display
		_playPauseButton = new Button(">");
		_playPauseButton.setMinWidth(30);
		_playPauseButton.setMaxWidth(30);
		//Function
		_playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				Status status = _player.getStatus();
				
				//Check status of media player and take appropriate action
				
				if (status == Status.UNKNOWN  || status == Status.HALTED) {
					//Don't do anything in these states
					return;
				}

				if ( status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {					
					_player.play();
				} else {
					_player.pause();
				}
			}
		});
		
		_bottomControls.getChildren().addAll(_playPauseButton);
		 
		
		//Time label
		Label timeLabel = new Label("Time: ");
		_bottomControls.getChildren().add(timeLabel);
		
		
		//Time slider
		_timeSlider = new Slider();
		HBox.setHgrow(_timeSlider,Priority.ALWAYS);
		_timeSlider.setMinWidth(50);
		_timeSlider.setMaxWidth(Double.MAX_VALUE);
		_timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (_timeSlider.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					_player.seek(_duration.multiply(_timeSlider.getValue() / 100.0));
				}
			}
		});
		_bottomControls.getChildren().add(_timeSlider);

		//Play label
		_playTime = new Label();
		_playTime.setPrefWidth(130);
		_playTime.setMinWidth(50);
		_bottomControls.getChildren().add(_playTime);
		 
		//Volume label
		Label volumeLabel = new Label("Vol: ");
		_bottomControls.getChildren().add(volumeLabel);
		 
		//Volume slider
		//Display
		_volSlider = new Slider();        
		_volSlider.setPrefWidth(70);
		_volSlider.setMaxWidth(Region.USE_PREF_SIZE);
		_volSlider.setMinWidth(30);
		//Function
		_volSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (_volSlider.isValueChanging()) {
					_player.setVolume(_volSlider.getValue() / 100.0);
				}
			}
		});
		_bottomControls.getChildren().add(_volSlider);
		
		_bottomControls.setSpacing(20);
		
		
		//Media player settings
		//Get total duration of video
		_player.setOnReady(new Runnable() {
			public void run() {
				_duration = _player.getMedia().getDuration();
				updateValues();
			}
		});
		//When playback is run
		_player.setOnPlaying(new Runnable() {
			public void run() {
				_playPauseButton.setText("||");
			}
		});
		//When playback is paused
		_player.setOnPaused(new Runnable() {
			public void run() {
				_playPauseButton.setText(">");
			}
		});
		//What to do when video ends
		_player.setOnEndOfMedia(new Runnable() {
			public void run() {	
				//Stop video playing, but set playback to beginning of video so user can repeat playback
				_playPauseButton.setText(">");
				_player.seek(_player.getStartTime());
				_player.pause();
			}
		});
		//Do something whenever playback time changes
		_player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});
		
		
		//Controls settings
		//When mouse enters window, show controls
		_mediaStackPane.setOnMouseEntered(mouseEnter -> {
			_controlsPane.setVisible(true);
			_controlsPane.setManaged(true);
		});
		
		//When mouse exits window, hide controls
		_mediaStackPane.setOnMouseExited(mouseExit -> {
			_controlsPane.setVisible(false);
			_controlsPane.setManaged(false);
		});
		
		
		
		//Setup scene and play media
		_mediaStackPane.getChildren().clear();
		_mediaStackPane.getChildren().addAll(_view, _controlsPane);

		_player.play();
	}
	
	protected void updateValues() {
		//Check for valid player setup
		if (_playTime != null && _timeSlider != null && _volSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					//Get current playback time and adjust relevant components
					Duration currentTime = _player.getCurrentTime();
					_playTime.setText(formatTime(currentTime, _duration));
					_timeSlider.setDisable(_duration.isUnknown());
					
					//Set time slider if applicable
					if (!_timeSlider.isDisabled() && _duration.greaterThan(Duration.ZERO) && !_timeSlider.isValueChanging()) {
						_timeSlider.setValue(currentTime.divide(_duration.toMillis()).toMillis() * 100.0);
					}
					
					//Set vol slider if applicable
					if (!_volSlider.isValueChanging()) {
						_volSlider.setValue((int)Math.round(_player.getVolume() * 100));
					}
				}
			});
		}
	}
	
	private static String formatTime(Duration elapsed, Duration duration) {
		//Get playback time components
		int intElapsed = (int)Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
				- elapsedMinutes * 60;
		
		//Display playback time
		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int)Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - 
					durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", 
						elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d",
						elapsedMinutes, elapsedSeconds,durationMinutes, 
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, 
						elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d",elapsedMinutes, 
						elapsedSeconds);
			}
		}
	}
	
	@FXML
	private void BackHandle(ActionEvent event) {
		_player.stop();
		CreationStore.destroy();
		
		try {
			changeScene((Node)event.getSource(), "/fxml/MainMenuPane.fxml");
		} catch (IOException e) {
			new ErrorAlert("Couldn't return to main menu");
		}
	}
}
