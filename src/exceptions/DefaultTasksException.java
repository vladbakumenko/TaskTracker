package exceptions;

public class DefaultTasksException extends RuntimeException {

    public DefaultTasksException() {
    }

    public DefaultTasksException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DefaultTasksException(String message) {
        super(message);
    }

    public DefaultTasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefaultTasksException(Throwable cause) {
        super(cause);
    }
}
