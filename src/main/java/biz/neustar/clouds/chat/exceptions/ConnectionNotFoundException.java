package biz.neustar.clouds.chat.exceptions;

public class ConnectionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8628290797220175201L;

	public ConnectionNotFoundException() {
		super();
	}

	public ConnectionNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionNotFoundException(String message) {
		super(message);
	}

	public ConnectionNotFoundException(Throwable cause) {
		super(cause);
	}
}
