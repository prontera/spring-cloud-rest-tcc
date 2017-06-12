package com.github.prontera.domain.type.handler;

import com.github.prontera.domain.type.EventStatus;

/**
 * @author Zhao Junjian
 */
public class EventStatusTypeHandler extends GenericTypeHandler<EventStatus> {
    @Override
    public int getEnumIntegerValue(EventStatus parameter) {
        return parameter.getStatus();
    }
}
