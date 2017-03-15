package com.github.prontera.model.type;

/**
 * @author Zhao Junjian
 */
public enum EventProcessStatus {
    NEW(0), PROCESSED(1), FAILED(2);

    private final int code;

    EventProcessStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
