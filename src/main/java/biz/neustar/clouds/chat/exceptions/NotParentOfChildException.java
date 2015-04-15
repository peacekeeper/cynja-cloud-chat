package biz.neustar.clouds.chat.exceptions;

public class NotParentOfChildException extends RuntimeException {

	private static final long serialVersionUID = 5107881366856213619L;

	public NotParentOfChildException() {
		super();
	}

	public NotParentOfChildException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotParentOfChildException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotParentOfChildException(String message) {
		super(message);
	}

	public NotParentOfChildException(Throwable cause) {
		super(cause);
	}
}
