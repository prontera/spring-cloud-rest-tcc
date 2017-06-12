package com.github.prontera.event;

import com.github.prontera.EventHandler;
import com.github.prontera.config.EventBusinessType;
import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.persistence.EventSubscriberMapper;
import com.github.prontera.util.Jacksons;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Zhao Junjian
 */
public class ExampleHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleHandler.class);

    private final EventHandler successor;
    private final EventSubscriberMapper mapper;

    public ExampleHandler(EventSubscriberMapper mapper, EventHandler successor) {
        this.mapper = mapper;
        this.successor = successor;
    }

    @Override
    public void handler(EventSubscriber subscriber) {
        Preconditions.checkNotNull(subscriber);
        Preconditions.checkNotNull(subscriber.getId());
        if (Objects.equals(EventBusinessType.ADD_PTS.name(), subscriber.getBusinessType())) {
            LOGGER.debug("consume: {}", Jacksons.parse(subscriber));
            mapper.updateEventStatusByPrimaryKeyInCasMode(subscriber.getId(), EventStatus.NEW, EventStatus.DONE);
        } else {
            if (successor != null) {
                successor.handler(subscriber);
            }
        }
    }
}
