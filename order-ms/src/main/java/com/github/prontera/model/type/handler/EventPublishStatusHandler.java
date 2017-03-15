package com.github.prontera.model.type.handler;

import com.github.prontera.common.model.typehandler.GenericTypeHandler;
import com.github.prontera.model.type.EventPublishStatus;

/**
 * @author Zhao Junjian
 */
public class EventPublishStatusHandler extends GenericTypeHandler<EventPublishStatus> {

    @Override
    public int getEnumIntegerValue(EventPublishStatus parameter) {
        return parameter.getCode();
    }
}
