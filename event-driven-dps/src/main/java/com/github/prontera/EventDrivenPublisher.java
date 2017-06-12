package com.github.prontera;

import com.github.prontera.domain.Event;
import com.github.prontera.domain.EventPublisher;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.persistence.EventPublisherMapper;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Zhao Junjian
 */
public class EventDrivenPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDrivenPublisher.class);

    @Autowired
    private EventPublisherMapper publisherMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final ConcurrentMap<String, MessageRoute> REGISTRY = new ConcurrentHashMap<>();

    /**
     * the basic.return is sent to the client before basic.ack
     */
    @PostConstruct
    public void postConstruct() {
        // return
        rabbitTemplate.setReturnCallback(new RabbitReturnCallback());
        // ack
        rabbitTemplate.setConfirmCallback(new RabbitConfirmCallback());
    }

    /**
     * 扫描定量的NEW事件，发布至Broker之后更新为PENDING
     */
    @Scheduled(fixedRate = 700)
    public void fetchAndPublishEventInNewStatus() {
        fetchAndPublishToBroker(PublishNewEventStrategy.SINGLETON);
    }

    /**
     * 扫面定量的PENDING事件并重新发布至Broker，意在防止实例因为意外宕机导致basic.return和basic.ack的状态丢失。
     */
    @Scheduled(fixedRate = 5000)
    public void fetchAndRepublishEventInPendingStatus() {
        fetchAndPublishToBroker(RepublishPendingEventStrategy.SINGLETON);
    }

    /**
     * 根据传入的业务类型取出所设定的exchange与routeKey
     */
    public static MessageRoute getMessageRoute(String businessType) {
        Preconditions.checkNotNull(businessType);
        return REGISTRY.get(businessType);
    }

    /**
     * 所有的业务类型都必须先注册exchange与routeKey才能使用，而不是将exchange与routeKey持久化，浪费大量磁盘空间。
     */
    public static void registerType(String businessType, String exchange, String routeKey) {
        Preconditions.checkNotNull(businessType);
        Preconditions.checkNotNull(exchange);
        Preconditions.checkNotNull(routeKey);
        REGISTRY.put(businessType, new MessageRoute(exchange, routeKey));
    }

    /**
     * 判断业务类型是否有被注册
     */
    public static boolean includesType(String businessType) {
        Preconditions.checkNotNull(businessType);
        return REGISTRY.containsKey(businessType);
    }

    public static void throwIfNotIncluded(String businessType) {
        Preconditions.checkNotNull(businessType);
        Preconditions.checkArgument(includesType(businessType), "该业务类型尚未注册");
    }

    /**
     * 消息落地
     */
    @Transactional(rollbackFor = Exception.class)
    public int persistPublishMessage(Object payload, String businessType) {
        Preconditions.checkNotNull(payload);
        Preconditions.checkNotNull(businessType);
        // 严格控制发往Broker的业务类型
        throwIfNotIncluded(businessType);
        final EventPublisher publisher = new EventPublisher();
        publisher.setEventStatus(EventStatus.NEW);
        publisher.setGuid(UUID.randomUUID().toString());
        publisher.setLockVersion(0);
        publisher.setPayload(Jacksons.parse(payload));
        publisher.setBusinessType(businessType);
        HibernateValidators.throwsIfInvalid(publisher);
        return publisherMapper.insertSelective(publisher);
    }

    /**
     * 发布消息至Broker，通常由定时器扫描发送
     */
    public void publish(Event event, String exchange, String routeKey, CorrelationData correlationData) {
        Preconditions.checkNotNull(event);
        Preconditions.checkNotNull(exchange);
        Preconditions.checkNotNull(routeKey);
        HibernateValidators.throwsIfInvalid(event);
        rabbitTemplate.convertAndSend(exchange, routeKey, event, correlationData);
    }

    /**
     * 按照指定的策略将指定状态的事件(通常为NEW与PENDING)发布至Broker
     */
    @Transactional(rollbackFor = Exception.class)
    public void fetchAndPublishToBroker(BatchFetchEventStrategy fetchStrategy) {
        Preconditions.checkNotNull(fetchStrategy);
        final Set<EventPublisher> events = fetchStrategy.execute(publisherMapper);
        for (EventPublisher event : events) {
            final String type = event.getBusinessType();
            if (includesType(type)) {
                // 发送
                final MessageRoute route = getMessageRoute(type);
                // DTO转换
                final Event dto = new Event();
                dto.setBusinessType(type);
                dto.setGuid(event.getGuid());
                dto.setPayload(event.getPayload());
                // 更新状态为'处理中'顺便刷新一下update_time
                event.setEventStatus(EventStatus.PENDING);
                // 意在多实例的情况下不要重复刷新
                if (publisherMapper.updateByPrimaryKeySelectiveWithOptimisticLock(event) > 0) {
                    // 正式发送至Broker
                    publish(dto, route.getExchange(), route.getRouteKey(), new CorrelationData(String.valueOf(event.getId())));
                }
            } else {
                // 将event status置为FAILED，等待人工处理
                event.setEventStatus(EventStatus.FAILED);
                if (publisherMapper.updateByPrimaryKeySelectiveWithOptimisticLock(event) > 0) {
                    LOGGER.warn("事件尚未注册不能被发送至Broker, id: {}, guid: {}，目前已将该事件置为FAILED，待审查过后人工将状态校正", event.getId(), event.getGuid());
                }
            }
        }
    }

    /**
     * 在Mandatory下，当exchange存在但无法路由至queue的情况下记录入库
     */
    private class RabbitReturnCallback implements RabbitTemplate.ReturnCallback {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            final String failedMessage = new String(message.getBody(), Charsets.UTF_8);
            try {
                final String guid = Jacksons.getMapper().readTree(failedMessage).get("guid").asText();
                final EventPublisher publisher = new EventPublisher();
                publisher.setGuid(guid);
                if (EventStatus.NO_ROUTE.name().equalsIgnoreCase(replyText)) {
                    publisher.setEventStatus(EventStatus.NO_ROUTE);
                } else {
                    logReturnedFault(replyCode, replyText, exchange, routingKey, failedMessage);
                    publisher.setEventStatus(EventStatus.ERROR);
                }
                // 因为在basic.return之后会调用basic.ack，鄙人认为NO_ROUTE的状态有可能被错误地转换成为NOT_FOUND，所以不需要考虑竞争情况
                publisherMapper.updateByGuidSelective(publisher);
            } catch (IOException e) {
                logReturnedFault(replyCode, replyText, exchange, routingKey, failedMessage);
            }
        }

        private void logReturnedFault(int replyCode, String replyText, String exchange, String routingKey, String failedMessage) {
            LOGGER.error("no route for message and failed to read it: {}, replyCode: {}, replyText: {}, " +
                    "exchange: {}, routeKey: {}", failedMessage, replyCode, replyText, exchange, routingKey);
        }
    }

    /**
     * 确认Broker接收消息的状态
     */
    private class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            final Long id = Long.valueOf(correlationData.getId());
            // 当一条消息为PENDING而且ack为true时则删除原有的消息
            if (ack) {
                // flag instead
                publisherMapper.updateEventStatusByPrimaryKeyInCasMode(id, EventStatus.PENDING, EventStatus.DONE);
                // 或直接删除
                //publisherMapper.deleteByPrimaryKey(id);
            } else {
                // 打开mandatory之后，ack为false的情况就是没有找到exchange
                LOGGER.error("message has failed to found a proper exchange which local id is {}. cause: {}", id, cause);
                publisherMapper.updateEventStatusByPrimaryKeyInCasMode(id, EventStatus.PENDING, EventStatus.NOT_FOUND);
            }
        }
    }

}
