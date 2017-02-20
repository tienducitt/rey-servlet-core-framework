/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 */
package com.reydentx.exception;

/**
 *
 * @author namnq
 */
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
