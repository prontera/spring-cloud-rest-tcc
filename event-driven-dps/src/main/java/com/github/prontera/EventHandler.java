package com.github.prontera;

import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.persistence.EventSubscriberMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zhao Junjian
 */
public abstract class EventHandler {
    @Autowired
    private EventSubscriberMapper mapper;

    public EventSubscriberMapper getMapper() {
        return mapper;
    }

    public void setMapper(EventSubscriberMapper mapper) {
        this.mapper = mapper;
    }

    public abstract void handle(EventSubscriber subscriber);
}
