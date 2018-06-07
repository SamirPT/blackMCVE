package util.exception;

/**
 * Created by arkady on 02/04/16.
 */
public class IllegalReportTypeException extends Exception {

	public IllegalReportTypeException() {
	}

	public IllegalReportTypeException(String message) {
		super(message);
	}

	public IllegalReportTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalReportTypeException(Throwable cause) {
		super(cause);
	}

	public IllegalReportTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
