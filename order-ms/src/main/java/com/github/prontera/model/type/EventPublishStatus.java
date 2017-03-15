package com.github.prontera.model.type;

/**
 * @author Zhao Junjian
 */
public enum EventPublishStatus {

    NEW(0), PUBLISHED(1), PENDING(2), DONE(3), FAILED(4);

    private final int code;

    EventPublishStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
