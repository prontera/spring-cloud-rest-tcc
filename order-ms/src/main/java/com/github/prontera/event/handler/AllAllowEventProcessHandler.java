package com.github.prontera.event.handler;

import com.github.prontera.common.model.response.Response;
import com.github.prontera.common.model.response.RestfulResponse;
import com.github.prontera.domain.EventProcess;

/**
 * @author Zhao Junjian
 */
public class AllAllowEventProcessHandler extends EventProcessHandler {

    @Override
    public Response execute(EventProcess event) {
        return new RestfulResponse();
    }

}
