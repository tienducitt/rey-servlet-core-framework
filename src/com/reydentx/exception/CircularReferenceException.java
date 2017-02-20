/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.exception;

/**
 *
 * @author ducnt3
 */
public class CircularReferenceException extends RuntimeException  {
        public CircularReferenceException() {
	}

	public CircularReferenceException(String message) {
		super(message);
	}

	public CircularReferenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public CircularReferenceException(Throwable cause) {
		super(cause);
	}
}
