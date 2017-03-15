package com.github.prontera.event.handler;

import com.github.prontera.common.model.response.Response;
import com.github.prontera.domain.EventProcess;

/**
 * @author Zhao Junjian
 */
public class NoOpsEventProcessHandler extends EventProcessHandler {

    @Override
    public Response execute(EventProcess event) {
        throw new IllegalStateException("this event does not match any handler. " + event);
    }

}
