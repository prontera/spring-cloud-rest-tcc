package com.github.prontera.model.type;

/**
 * @author Zhao Junjian
 */
public enum OrderStatus {

    PROCESSING(0), DONE(1), INSUFFICIENT_BALANCE(2), INSUFFICIENT_STOCK(3), TIMEOUT(4), CONFLICT(5);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
