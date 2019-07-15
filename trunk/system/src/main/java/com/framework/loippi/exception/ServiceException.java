package com.framework.loippi.exception;

/**
 * Exception Service
 * 
 * @author Loippi Team
 * @version 1.0
 */
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 304586860245241104L;

	public ServiceException() {
	}

	public ServiceException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public ServiceException(String paramString) {
		super(paramString);
	}

	public ServiceException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}