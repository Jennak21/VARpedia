package controllers;

import application.Creation;

public class QuizResult {
	private Creation _creation;
	private String _creationName;
	private int _numCorrect;
	private int _totalNum;
	private int _deltaTestAcc;
	private String _searchTerm;
	private String _translatedTerm;
	
	public QuizResult(Creation creation) {
		_creation = creation;
		_creationName = _creation.getFilename();
		_searchTerm = _creation.getSearchTerm();
		_numCorrect = 0;
		_totalNum = 0;
	}
	
	public void setTranslatedTerm(String term) {
		_translatedTerm = term;
	}
	
	public String getSearchTerm() {
		return _searchTerm;
	}
	
	public String getTranslatedTerm() {
		return _translatedTerm;
	}
	
	public void addResult(boolean correct) {
		_totalNum++;
		
		if (correct) {
			_numCorrect++;
		}
	}
	
	public void calculateDeltaTestAcc() {
		int oldTestAcc = _creation.getIntAcc();
		double quizTestAcc = getResultStat() * 100;
		int newTestAcc = (int)((oldTestAcc + quizTestAcc)/2);
		
		_deltaTestAcc = newTestAcc - oldTestAcc;
	}
	
	public int getDeltaTestAcc() {
		return _deltaTestAcc;
	}
	
	public String getLearning() {
		String resultString = "";
		
		if (_deltaTestAcc > 0) {
			resultString = "+";
		}
		resultString = resultString + _deltaTestAcc + "%";
		
		return resultString;
	}
	
	public String getResultString() {
		return _numCorrect + "/" + _totalNum;
	}
	
	public double getResultStat() {
		return (double)(_numCorrect) / (double)(_totalNum);
	}
	
	public Creation getCreation() {
		return _creation;
	}
	
	public String getCreationName() {
		return _creationName;
	}
	
	public boolean sameCreation(Creation creation) {
		return _creation.getFilename().equals(creation.getFilename());
	}
	
	public boolean sameFileCreation(String filename) {
		return filename.equals(_creation.getFilename());
	}
}
