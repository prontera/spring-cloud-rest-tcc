package com.github.prontera.model.type.handler;

import com.github.prontera.common.model.typehandler.GenericTypeHandler;
import com.github.prontera.model.type.EventType;

/**
 * @author Zhao Junjian
 */
public class EventPublishTypeHandler extends GenericTypeHandler<EventType> {

    @Override
    public int getEnumIntegerValue(EventType parameter) {
        return parameter.getCode();
    }
}
