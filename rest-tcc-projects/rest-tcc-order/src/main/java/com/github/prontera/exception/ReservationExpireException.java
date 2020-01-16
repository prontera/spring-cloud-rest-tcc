package com.github.prontera.exception;

import feign.Response;

/**
 * @author Zhao Junjian
 */
public class ReservationExpireException extends RuntimeException {

    private static final long serialVersionUID = -1175850219159714505L;

    private Response response;

    public ReservationExpireException(String message) {
        super(message);
    }

    public ReservationExpireException(String message, Response response) {
        super(message);
        this.response = response;
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

    public Response getResponse() {
        return response;
    }
}
