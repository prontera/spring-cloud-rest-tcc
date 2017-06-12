package com.github.prontera;

import com.github.prontera.domain.Event;
import com.github.prontera.domain.EventPublisher;
import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.persistence.EventSubscriberMapper;
import com.github.prontera.util.HibernateValidators;
import com.github.prontera.util.Jacksons;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * @author Zhao Junjian
 */
//@Component
public class EventDrivenSubscriber implements ApplicationEventPublisherAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDrivenSubscriber.class);

    @Autowired
    private EventSubscriberMapper subscriberMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private ApplicationEventPublisher springEventPublisher;
    private EventHandler handler;

    public EventDrivenSubscriber(EventHandler handler) {
        this.handler = handler;
    }

    /**
     * 扫面定量的PENDING事件并重新发布至Broker，意在防止实例因为意外宕机导致basic.return和basic.ack的状态丢失。
     */
    @Scheduled(fixedRate = 5000)
    public void fetchAndRepublishEventInPendingStatus() {
        //fetchAndPublishToBroker(RepublishPendingEventStrategy.SINGLETON);
    }

    @Transactional(rollbackFor = Exception.class)
    public int persistSubscribeMessage(String businessType, String payload, String guid) {
        Preconditions.checkNotNull(businessType);
        Preconditions.checkNotNull(payload);
        Preconditions.checkNotNull(guid);
        final EventSubscriber subscriber = new EventSubscriber();
        subscriber.setBusinessType(businessType);
        subscriber.setPayload(payload);
        subscriber.setGuid(guid);
        subscriber.setLockVersion(0);
        subscriber.setEventStatus(EventStatus.NEW);
        HibernateValidators.throwsIfInvalid(subscriber);
        return subscriberMapper.insertSelective(subscriber);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.springEventPublisher = applicationEventPublisher;
    }

}
