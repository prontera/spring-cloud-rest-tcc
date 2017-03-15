package com.github.prontera.model.type.handler;

import com.github.prontera.common.model.typehandler.GenericTypeHandler;
import com.github.prontera.model.type.EventProcessStatus;

/**
 * @author Zhao Junjian
 */
public class EventProcessStatusHandler extends GenericTypeHandler<EventProcessStatus> {

    @Override
    public int getEnumIntegerValue(EventProcessStatus parameter) {
        return parameter.getCode();
    }

}
