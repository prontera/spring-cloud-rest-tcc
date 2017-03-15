package com.github.prontera.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.prontera.common.exception.PayloadTooLongException;
import com.github.prontera.common.model.response.ErrorEntity;
import com.github.prontera.common.model.response.Response;
import com.github.prontera.common.util.Jacksons;
import com.github.prontera.domain.EventProcess;
import com.github.prontera.domain.EventPublish;
import com.github.prontera.event.handler.EventProcessHandler;
import com.github.prontera.model.event.EventPublishPreparation;
import com.github.prontera.model.type.EventProcessStatus;
import com.github.prontera.model.type.EventPublishStatus;
import com.github.prontera.model.type.EventType;
import com.github.prontera.service.EventProcessService;
import com.github.prontera.service.EventPublishService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @author Zhao Junjian
 */
@Component
public class TaskDistributionListener implements ApplicationListener<TaskDistributionEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDistributionListener.class);

    private final EventProcessHandler handlerChain;
    private final EventPublishService publishService;
    private final EventProcessService processService;
    private static final String ORIGIN_PUBLISH_GUID = "origin_publish_guid";
    private static final String PROCESS_GUID = "process_guid";
    private static final String RESULT = "result";

    @Autowired
    public TaskDistributionListener(EventProcessHandler handlerChain, EventPublishService publishService, EventProcessService processService) {
        this.handlerChain = handlerChain;
        this.publishService = publishService;
        this.processService = processService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onApplicationEvent(TaskDistributionEvent event) {
        final EventProcess source = (EventProcess) event.getSource();
        // 获取处理后的实体, 有可能是ErrorEntity或是RestfulResponse
        final Response response = handlerChain.execute(source);
        // 判断是否为ErrorEntity, 是则为处理错误
        source.setEventStatus(EventProcessStatus.PROCESSED);
        if (response instanceof ErrorEntity) {
            source.setEventStatus(EventProcessStatus.FAILED);
        }
        // 记录处理后的实体
        source.setRespPayload(Jacksons.parse(response));
        final int influence = processService.updateNonNullPropertiesWithOptLock(source, source.getUpdateTime());
        // 处理REQUEST或RESPONSE类型事件
        afterProcessing(influence, source, response);
    }

    /**
     * 判断更新的状态是否有效, 然后再处理事件类型, 已决定是否发送确定事件
     */
    private void afterProcessing(int influence, EventProcess source, Response repsonse) {
        if (influence > 0) {
            Preconditions.checkNotNull(source);
            handlingWithEventType(source, repsonse);
        } else {
            LOGGER.info("event '{}' was failed to update with optimize lock.", source.getId());
        }
    }

    private void handlingWithEventType(EventProcess source, Response response) {
        Preconditions.checkNotNull(source);
        if (source.getEventType() == EventType.REQUEST) {
            makeResponseToOriginalPublisher(source, response);
        } else if (source.getEventType() == EventType.RESPONSE) {
            markOriginalRequestAsDone(source);
        }
    }

    private void makeResponseToOriginalPublisher(EventProcess source, Response response) {
        Preconditions.checkNotNull(source);
        final String bizType = source.getBizType();
        final ImmutableMap<String, Object> payload = ImmutableMap.of(ORIGIN_PUBLISH_GUID, source.getPublishGuid(),
                PROCESS_GUID, source.getProcessGuid(),
                RESULT, response);
        final EventPublishPreparation preparation = EventPublishPreparation.builder()
                .bizType(bizType)
                .eventType(EventType.RESPONSE)
                .payload(payload).build();
        try {
            publishService.persistMessage(preparation);
        } catch (PayloadTooLongException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 从req_payload中获取服务方处理的publish_guid和process_guid
     */
    private void markOriginalRequestAsDone(EventProcess source) {
        Preconditions.checkNotNull(source);
        try {
            final JsonNode jsonNode = Jacksons.getMapper().readTree(source.getReqPayload());
            if (jsonNode == null) {
                throw new IllegalStateException("request payload could not be tranforming into JSON. entity: " + source);
            }
            // 请求方, 即本库t_publish中的guid
            final String originPublishGuid = jsonNode.get(ORIGIN_PUBLISH_GUID).asText("");
            if (originPublishGuid.isEmpty()) {
                throw new IllegalStateException(ORIGIN_PUBLISH_GUID + " was not included in request payload.");
            }
            final int code = jsonNode.get(RESULT).get("code").asInt(0);
            if (code == 0) {
                // 服务方中t_process的guid
                final String processGuid = jsonNode.get(PROCESS_GUID).asText();
                throw new IllegalStateException("invalid message structure, the code has dismissed from remote process guid " + processGuid);
            }
            final EventPublish eventPublish = publishService.findByGuid(originPublishGuid);
            if (eventPublish == null) {
                throw new IllegalArgumentException("publish event does not exist. id " + originPublishGuid);
            } else {
                // 根据code判断是否为失败
                if (code >= 20000 && code < 30000) {
                    eventPublish.setEventStatus(EventPublishStatus.DONE);
                } else {
                    eventPublish.setEventStatus(EventPublishStatus.FAILED);
                }
                // 设置为本地库的process_guid
                eventPublish.setProcessGuid(source.getProcessGuid());
                publishService.updateNonNullPropertiesWithOptLock(eventPublish, eventPublish.getUpdateTime());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
