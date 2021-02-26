package cz.muni.ics.perunproxyapi.persistence.exceptions;

/**
 * Exception represents problem when performing any DB operation.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class DBOperationException extends Exception {
    public DBOperationException() {
        super();
    }

    public DBOperationException(String message) {
        super(message);
    }

    public DBOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBOperationException(Throwable cause) {
        super(cause);
    }

    protected DBOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
