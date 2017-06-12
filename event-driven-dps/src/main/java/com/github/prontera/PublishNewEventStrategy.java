package com.github.prontera;

import com.github.prontera.domain.EventPublisher;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.persistence.EventPublisherMapper;

import java.util.Set;

/**
 * @author Zhao Junjian
 */
public enum PublishNewEventStrategy implements BatchFetchEventStrategy {
    SINGLETON;

    @Override
    public Set<EventPublisher> execute(EventPublisherMapper mapper) {
        return mapper.selectLimitedEntityByEventStatus(EventStatus.NEW, 300);
    }
}
