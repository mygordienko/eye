package org.eye.exceptions;

/**
 * Created by mygordienko on 04.10.2014.
 */
public class EyeWatcherException extends Exception {
	public EyeWatcherException() {
	}

	public EyeWatcherException(String message) {
		super(message);
	}

	public EyeWatcherException(String message, Throwable cause) {
		super(message, cause);
	}

	public EyeWatcherException(Throwable cause) {
		super(cause);
	}

	public EyeWatcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
