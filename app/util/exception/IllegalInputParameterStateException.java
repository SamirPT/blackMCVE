package util.exception;

/**
 * Created by arkady on 02/04/16.
 */
public class IllegalInputParameterStateException extends Exception {
	public IllegalInputParameterStateException() {
	}

	public IllegalInputParameterStateException(String message) {
		super(message);
	}

	public IllegalInputParameterStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalInputParameterStateException(Throwable cause) {
		super(cause);
	}

	public IllegalInputParameterStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
