package com.github.prontera.model.type;

/**
 * @author Zhao Junjian
 */
public enum OrderStatus {

    PROCESSING(0), DONE(1), IN_RETURN(2), RETURN(3), FAIL(4);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
