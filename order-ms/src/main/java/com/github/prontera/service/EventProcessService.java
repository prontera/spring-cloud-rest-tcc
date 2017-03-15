package com.github.prontera.service;

import com.github.prontera.common.exception.PayloadTooLongException;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.common.service.CrudServiceImpl;
import com.github.prontera.common.util.GuidGenerator;
import com.github.prontera.common.util.HibernateValidators;
import com.github.prontera.common.util.Jacksons;
import com.github.prontera.config.RabbitConfiguration;
import com.github.prontera.domain.EventProcess;
import com.github.prontera.event.TaskDistributionEvent;
import com.github.prontera.model.event.EventProcessPreparation;
import com.github.prontera.model.type.EventProcessStatus;
import com.github.prontera.persistence.EventProcessMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Zhao Junjian
 */
@Service
public class EventProcessService extends CrudServiceImpl<EventProcess> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessService.class);

    @Autowired
    private EventProcessMapper mapper;
    @Autowired
    private ApplicationContext context;

    @Autowired
    public EventProcessService(CrudMapper<EventProcess> mapper) {
        super(mapper);
    }

    /**
     * 记录获取request payload, process_guid和process_payload在定时任务中处理
     *
     * @throws PayloadTooLongException 当request payload过长的时候抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public int persistMessage(EventProcessPreparation response) throws PayloadTooLongException {
        Preconditions.checkNotNull(response);
        HibernateValidators.throwsIfInvalid(response);
        // 转换成JSON存入数据库
        final String payloadJson = Jacksons.parse(response.getPayload());
        // 转换后的JSON的大小约束
        if (payloadJson.length() > RabbitConfiguration.PAYLOAD_MAXIMUM_LENGTH) {
            throw new PayloadTooLongException("message length is " + payloadJson.length() + " which is limited to " + RabbitConfiguration.PAYLOAD_MAXIMUM_LENGTH);
        }
        final EventProcess event = new EventProcess();
        event.setBizType(response.getBizType());
        event.setEventType(response.getEventType());
        event.setEventStatus(EventProcessStatus.NEW);
        event.setReqPayload(payloadJson);
        event.setPublishGuid(response.getPublishGuid());
        final String guid = GuidGenerator.generate();
        event.setProcessGuid(guid);
        HibernateValidators.throwsIfInvalid(event);
        LOGGER.debug("Process Message is being persisting. {}", event);
        try {
            return persistNonNullProperties(event);
        } catch (DuplicateKeyException e) {
            LOGGER.error("duplicate key in processing message '{}'. {}", guid, e);
            return 0;
        }
    }

    /**
     * 统一发布事件, 在监听器中按照事件类型分发给不同的处理者
     */
    @Scheduled(fixedDelay = 300)
    public void processTask() {
        final List<EventProcess> events = mapper.selectByEventStatus(EventProcessStatus.NEW, 30);
        for (EventProcess event : events) {
            context.publishEvent(new TaskDistributionEvent(event));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateNonNullPropertiesWithOptLock(EventProcess entity, OffsetDateTime oldUpdateTime) {
        Preconditions.checkNotNull(entity, "entity for updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
        return mapper.updateByPrimaryKeySelectiveWithUpdateTimeOptLock(entity, oldUpdateTime);
    }

}
