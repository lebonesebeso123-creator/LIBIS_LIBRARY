package com.libis.exception;

/**
 * Unchecked wrapper for SQLException, thrown by the DAOs after logging the real cause.
 * web.xml maps it to a friendly error page, so a stack trace never reaches the browser.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
