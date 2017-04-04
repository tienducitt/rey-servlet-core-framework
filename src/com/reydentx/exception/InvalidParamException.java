
package com.reydentx.exception;

public class InvalidParamException extends Exception {

	public InvalidParamException() {
		super();
	}

	public InvalidParamException(String message) {
		super(message);
	}

	public InvalidParamException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParamException(Throwable cause) {
		super(cause);
	}
}
