package com.github.prontera.config;

/**
 * @author Zhao Junjian
 */
public final class RequestAttributeConst {
    public static final String DETAILS_KEY = "X-Logs-Details";
    public static final String REQUEST_BODY_KEY = "X-Request-Body";
    public static final String REQUEST_ID = "X-Request-Id";

    private RequestAttributeConst() {
        throw new IllegalStateException("do not try to use reflection");
    }

}
