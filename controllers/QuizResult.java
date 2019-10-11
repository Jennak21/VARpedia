package controllers;

import application.Creation;

public class QuizResult {
	private Creation _creation;
	private int _numCorrect;
	private int _totalNum;
	
	public QuizResult(Creation creation) {
		_creation = creation;
		_numCorrect = 0;
		_totalNum = 0;
	}
	
	public void addResult(boolean correct) {
		_totalNum++;
		
		if (correct) {
			_numCorrect++;
		}
	}
	
	public String getResultString() {
		return _creation.getSearchTerm() + ": " + _numCorrect + "/" + _totalNum;
	}
	
	public double getResultStat() {
		return (double)(_numCorrect) / (double)(_totalNum);
	}
	
	public Creation getCreation() {
		return _creation;
	}
	
	public boolean sameCreation(Creation creation) {
		return _creation.getFilename().equals(creation.getFilename());
	}
	
	public boolean sameFileCreation(String filename) {
		return filename.equals(_creation.getFilename());
	}
}
