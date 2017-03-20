package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class PayloadTooLongException extends Exception {
    private static final long serialVersionUID = 8663242515151031941L;

    public PayloadTooLongException() {
    }

    public PayloadTooLongException(String message) {
        super(message);
    }

    public PayloadTooLongException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayloadTooLongException(Throwable cause) {
        super(cause);
    }

    protected PayloadTooLongException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
