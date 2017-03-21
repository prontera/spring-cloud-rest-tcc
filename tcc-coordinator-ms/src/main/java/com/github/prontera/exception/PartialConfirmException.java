package com.github.prontera.exception;

import com.github.prontera.model.TccErrorResponse;

/**
 * @author Zhao Junjian
 */
public class PartialConfirmException extends RuntimeException {

    private static final long serialVersionUID = 3665563233664481931L;

    private TccErrorResponse errorResponse;

    public PartialConfirmException(String message) {
        super(message);
    }

    public PartialConfirmException(String message, TccErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
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

    public TccErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
