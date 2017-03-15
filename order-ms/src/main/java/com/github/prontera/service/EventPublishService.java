package com.github.prontera.service;

import com.github.prontera.common.exception.PayloadTooLongException;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.common.service.CrudServiceImpl;
import com.github.prontera.common.util.GuidGenerator;
import com.github.prontera.common.util.HibernateValidators;
import com.github.prontera.common.util.Jacksons;
import com.github.prontera.config.RabbitConfiguration;
import com.github.prontera.domain.EventPublish;
import com.github.prontera.model.event.EventPublishPreparation;
import com.github.prontera.model.event.RouteMapping;
import com.github.prontera.model.type.EventPublishStatus;
import com.github.prontera.model.type.EventType;
import com.github.prontera.persistence.EventPublishMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Zhao Junjian
 */
@Service
public class EventPublishService extends CrudServiceImpl<EventPublish> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishService.class);

    @Autowired
    private EventPublishMapper mapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    public EventPublishService(CrudMapper<EventPublish> mapper) {
        super(mapper);
    }

    public EventPublish findByGuid(String guid) {
        Preconditions.checkNotNull(guid);
        return mapper.selectByPublishGuid(guid);
    }

    @Transactional(rollbackFor = Exception.class)
    public int persistMessage(EventPublishPreparation request) throws PayloadTooLongException {
        Preconditions.checkNotNull(request);
        HibernateValidators.throwsIfInvalid(request);
        // 转换成JSON存入数据库
        final String payloadJson = Jacksons.parse(request.getPayload());
        // 转换后的JSON的大小约束
        if (payloadJson.length() > RabbitConfiguration.PAYLOAD_MAXIMUM_LENGTH) {
            throw new PayloadTooLongException("message length is " + payloadJson.length() + " which is limited to " + RabbitConfiguration.PAYLOAD_MAXIMUM_LENGTH);
        }
        final EventPublish event = new EventPublish();
        event.setBizType(request.getBizType());
        event.setEventType(request.getEventType());
        event.setEventStatus(EventPublishStatus.NEW);
        event.setPayload(payloadJson);
        final String guid = GuidGenerator.generate();
        event.setPublishGuid(guid);
        HibernateValidators.throwsIfInvalid(event);
        LOGGER.debug("Publish Message is being persisting. {}", event);
        return persistNonNullProperties(event);
    }

    /**
     * 从库中批量取出发布任务, 根据biz_type获取对应的exchange与route key进行发布, 然后更新发布任务的状态
     */
    @Scheduled(fixedDelay = 300)
    @Transactional(rollbackFor = Exception.class)
    public void publishMessage() {
        final List<EventPublish> events = mapper.selectByEventStatus(EventPublishStatus.NEW, 30);
        for (EventPublish event : events) {
            final RouteMapping mapping = RouteMapping.parse(event.getBizType());
            // 当路由正确被解析时构建消息请求
            if (mapping != null) {
                if (event.getEventType() == EventType.REQUEST) {
                    event.setEventStatus(EventPublishStatus.PENDING);
                } else {
                    event.setEventStatus(EventPublishStatus.PUBLISHED);
                }
                final OffsetDateTime oldUpdateTime = event.getUpdateTime();
                event.setUpdateTime(OffsetDateTime.now());
                // 若被其他线程所处理则不再进行发送
                final int influence = updateNonNullPropertiesWithOptLock(event, oldUpdateTime);
                if (influence > 0) {
                    event.setCreateTime(null);
                    event.setUpdateTime(null);
                    event.setDeleteTime(null);
                    event.setEventStatus(null);
                    //amqpTemplate.send(mapping.getExchange(), mapping.getRouteKey(), new Message(event.getPayload().getBytes(Charsets.UTF_8), DEFAULT_MESSAGE_PROPERTIES));
                    amqpTemplate.convertAndSend(mapping.getExchange(), mapping.getRouteKey(), event);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateNonNullPropertiesWithOptLock(EventPublish entity, OffsetDateTime oldUpdateTime) {
        Preconditions.checkNotNull(entity, "entity for updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
        return mapper.updateByPrimaryKeySelectiveWithUpdateTimeOptLock(entity, oldUpdateTime);
    }

}
