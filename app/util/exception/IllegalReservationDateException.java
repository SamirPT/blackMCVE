package util.exception;

/**
 * Created by arkady on 19/03/16.
 */
public class IllegalReservationDateException extends Exception {

	public IllegalReservationDateException() {
	}

	public IllegalReservationDateException(String message) {
		super(message);
	}

	public IllegalReservationDateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalReservationDateException(Throwable cause) {
		super(cause);
	}

	public IllegalReservationDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
