package com.github.prontera.model.type;

/**
 * @author Zhao Junjian
 */
public enum EventType {

    NOTIFY(0), REQUEST(1), RESPONSE(2);

    private final int code;

    EventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
