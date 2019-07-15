package com.framework.loippi.exception;

/**
 * Exception Framework
 * 
 * @author Loippi Team
 * @version 1.0
 */
public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 6589945563856576000L;

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(Throwable cause) {
		super(null, cause);
	}
}