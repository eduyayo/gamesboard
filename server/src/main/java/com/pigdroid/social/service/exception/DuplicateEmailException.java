package com.pigdroid.social.service.exception;

/**
 * The exception is thrown when the email given during the registration
 * phase is already found from the database.
 * @author eduyayo@gmail.com
 */
public class DuplicateEmailException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4809161783401074982L;

	public DuplicateEmailException(String message) {
        super(message);
    }
}
