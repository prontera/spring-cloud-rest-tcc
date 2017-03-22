package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class ReservationExpireException extends RuntimeException {
    private static final long serialVersionUID = 2180330687914729827L;

    public ReservationExpireException() {
    }

    public ReservationExpireException(String message) {
        super(message);
    }

    public ReservationExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservationExpireException(Throwable cause) {
        super(cause);
    }

    protected ReservationExpireException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
