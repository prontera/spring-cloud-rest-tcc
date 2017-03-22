package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class PartialConfirmException extends RuntimeException {

    private static final long serialVersionUID = -736692887311988416L;

    public PartialConfirmException(String message) {
        super(message);
    }

    public PartialConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public PartialConfirmException(Throwable cause) {
        super(cause);
    }

    protected PartialConfirmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
