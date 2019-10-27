package application;

/**
 * Quiz info for creation
 * @author Max Gurr & Jenna Kumar
 *
 */
public class QuizResult {
	private Creation _creation;
	private String _creationName;
	private int _numCorrect;
	private int _totalNum;
	private int _deltaTestAcc;
	private String _term;
	
	/**
	 * Constructor
	 * @param creation - Creation for this result
	 */
	public QuizResult(Creation creation) {
		//Initialise fields
		_creation = creation;
		_creationName = _creation.getFilename();
		_numCorrect = 0;
		_totalNum = 0;
	}
	
	public void setTerm(String term) {
		_term = term;
	}
	public String getTerm() {
		return _term;
	}
	
	/**
	 * Add question result
	 * @param correct - Whether user got question correct or not
	 */
	public void addResult(boolean correct) {
		//Increase total number of questions asked for this creation
		_totalNum++;
		
		//If user got it correct, increase num questions correct
		if (correct) {
			_numCorrect++;
		}
	}
	
	/**
	 * Calculate change in test accuracy for this creation
	 */
	public void calculateDeltaTestAcc() {
		//Get accuracy values
		int oldTestAcc = _creation.getIntAcc();
		double quizTestAcc = getResultStat() * 100;
		
		//Calculate new accuracy
		int newTestAcc = (int)((oldTestAcc + quizTestAcc)/2);
		
		//Calculate change in test accuracy
		_deltaTestAcc = newTestAcc - oldTestAcc;
	}
	
	public int getDeltaTestAcc() {
		return _deltaTestAcc;
	}
	
	/**
	 * Get learning display for quiz results
	 * @return String - Representing change in learning %
	 */
	public String getLearning() {
		String resultString = "";
		
		//Construct and return string
		if (_deltaTestAcc > 0) {
			resultString = "+";
		}
		resultString = resultString + _deltaTestAcc + "%";
		
		return resultString;
	}
	
	/**
	 * Check whether this result's creation is same as given creation
	 * @param String - Creation filename to compare this result's creation to
	 * @return boolean - Same creation or not
	 */
	public boolean sameFileCreation(String filename) {
		return filename.equals(_creation.getFilename());
	}
	
	/**
	 * Get string representing total results for this creation
	 * @return String - Result "x/y" where x = num correct and y = total num of questions
	 */
	public String getResultString() {
		return _numCorrect + "/" + _totalNum;
	}
	
	/**
	 * Get result statistic
	 * @return double - Decimal accuracy for this creation in quiz
	 */
	public double getResultStat() {
		return (double)(_numCorrect) / (double)(_totalNum);
	}
	
	public Creation getCreation() {
		return _creation;
	}
	
	public String getCreationName() {
		return _creationName;
	}
}
