package com.github.prontera.event.handler;

import com.github.prontera.common.model.response.Response;
import com.github.prontera.domain.EventProcess;

/**
 * @author Zhao Junjian
 */
public abstract class EventProcessHandler {
    private EventProcessHandler successor;

    public abstract Response execute(EventProcess event);

    public void setSuccessor(EventProcessHandler successor) {
        this.successor = successor;
    }

    public EventProcessHandler getSuccessor() {
        return successor;
    }
}
