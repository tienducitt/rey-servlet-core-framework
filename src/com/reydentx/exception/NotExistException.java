/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 */
package com.reydentx.exception;

/**
 *
 * @author namnq
 */
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
