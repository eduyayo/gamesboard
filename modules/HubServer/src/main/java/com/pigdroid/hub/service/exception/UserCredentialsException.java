package com.pigdroid.hub.service.exception;

public class UserCredentialsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserCredentialsException() {
		super();
	}

	public UserCredentialsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserCredentialsException(String message) {
		super(message);
	}

	public UserCredentialsException(Throwable cause) {
		super(cause);
	}
	
	

}
