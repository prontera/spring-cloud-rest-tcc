package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class ReservationAlmostToExpireException extends RuntimeException {

    private static final long serialVersionUID = 2336784357948344886L;

    public ReservationAlmostToExpireException(String message) {
        super(message);
    }

    public ReservationAlmostToExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservationAlmostToExpireException(Throwable cause) {
        super(cause);
    }

    protected ReservationAlmostToExpireException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
