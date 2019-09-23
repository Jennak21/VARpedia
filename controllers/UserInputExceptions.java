package controllers;

public class UserInputExceptions extends RuntimeException{

	public UserInputExceptions (String message) {
		super("Error: " + message);
	}

}
