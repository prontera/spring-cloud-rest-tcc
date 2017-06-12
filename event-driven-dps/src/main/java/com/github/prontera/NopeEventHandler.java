package com.github.prontera;

import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.persistence.EventSubscriberMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zhao Junjian
 */
public class NopeEventHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NopeEventHandler.class);

    private final EventSubscriberMapper subscriberMapper;

    public NopeEventHandler(EventSubscriberMapper subscriberMapper) {
        this.subscriberMapper = subscriberMapper;
    }

    @Override
    public void handler(EventSubscriber subscriber) {
        Preconditions.checkNotNull(subscriber);
        Preconditions.checkNotNull(subscriber.getId());
        if (subscriberMapper.updateEventStatusByPrimaryKeyInCasMode(subscriber.getId(), EventStatus.NEW, EventStatus.NOT_FOUND) > 0) {
            LOGGER.error("event which id is {} has to change status from NEW to NOT_FOUND due to threr is not a match handler.", subscriber.getId());
        }
    }

}
