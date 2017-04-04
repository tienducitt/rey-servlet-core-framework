package com.reydentx.exception;

public class NotExistException extends Exception {

	public NotExistException() {
		super();
	}

	public NotExistException(String message) {
		super(message);
	}

	public NotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotExistException(Throwable cause) {
		super(cause);
	}
}
