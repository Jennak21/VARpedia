package controllers;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class AudioPlayer {
	private StackPane _stackPane;
	private BorderPane _controlsPane;
	private VBox _controlsBar;
	
	private Media _media;
	private MediaPlayer _player;
	private MediaView _view;
	private Duration _duration;
	
	private Duration _playbackTime;
	
	private Button _playPauseButton;
	private Slider _timeSlider;
	
	public AudioPlayer(StackPane stackPane, BorderPane controlsPane, VBox controlsBar) {
		_stackPane = stackPane;
		_controlsPane = controlsPane;
		_controlsBar = controlsBar;
		
		//Add all controls
    	_playPauseButton = new Button(">");
    	_controlsBar.getChildren().addAll(_playPauseButton);
    	
		
		_timeSlider = new Slider();
		_controlsBar.getChildren().add(_timeSlider);
		
		
		_controlsBar.setSpacing(20);
	}
	
	private void setupPlayer() {
		_player = new MediaPlayer(_media);
		_view = new MediaView(_player);
		
		_playbackTime = _playbackTime = _player.getStartTime();;
		
    	//Play/pause button
		//Display
		_playPauseButton.setMinWidth(50);
		_playPauseButton.setMaxWidth(50);
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
					_player.seek(_playbackTime);
					_player.play();
				} else {
					_playbackTime = _player.getCurrentTime();
					_player.pause();
				}
			}
		});
		
		//Time slider
		HBox.setHgrow(_timeSlider,Priority.ALWAYS);
		_timeSlider.setMinWidth(50);
		_timeSlider.setMaxWidth(Double.MAX_VALUE);
		_timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (_timeSlider.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					_playbackTime = _duration.multiply(_timeSlider.getValue() / 100.0);
					_player.seek(_duration.multiply(_timeSlider.getValue() / 100.0));
				}
			}
		});
		
		
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
				_playbackTime = _player.getStartTime();
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
		
		
		//Setup scene and play media
		_stackPane.getChildren().clear();
		_stackPane.getChildren().addAll(_view, _controlsPane);
		
		double _viewHeight = _stackPane.getHeight();
		double _viewWidth = _stackPane.getWidth();
		
		_view.setFitHeight(_viewHeight);
		_view.setFitWidth(_viewWidth);
    }
	
	private void updateValues() {
		//Check for valid player setup
		if (_timeSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					//Get current playback time and adjust relevant components
					Duration currentTime = _player.getCurrentTime();
					_timeSlider.setDisable(_duration.isUnknown());
					
					//Set time slider if applicable
					if (!_timeSlider.isDisabled() && _duration.greaterThan(Duration.ZERO) && !_timeSlider.isValueChanging()) {
						_timeSlider.setValue(currentTime.divide(_duration.toMillis()).toMillis() * 100.0);
					}
				}
			});
		}
	}
	
	public void setMedia(String filepath) {
    	//Load new media into player
    	_media = new Media("file://" + filepath);
		
    	//Adjust controls to fit new media
		setupPlayer();
    }
	
	public void play() {
		_player.play();
	}
}
