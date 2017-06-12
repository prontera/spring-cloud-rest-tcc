package com.github.prontera;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.prontera.config.EventBusinessType;
import com.github.prontera.config.RabbitConfiguration;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

/**
 * @author Zhao Junjian
 */
@RestController
public class RabbitController {
    @Autowired
    private RabbitTemplate amqpTemplate;
    @Autowired
    private EventDrivenPublisher eventBus;
    @Autowired
    private EventDrivenSubscriber subscriber;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitController.class);

    //@PostConstruct
    public void init() {
        amqpTemplate.setConfirmCallback((correlationData, ack, cause) -> LOGGER.info("ACK:: correlationData: {}, ack: {}, cause: {}", correlationData, ack, cause));
        amqpTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> LOGGER.info("RETURN:: message: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}", new String(message.getBody(), Charsets.UTF_8), replyCode, replyText, exchange, routingKey));
    }

    @RequestMapping(value = "/queue-exists", method = RequestMethod.GET)
    public Map<String, ?> queueExist() {
        final Foo unit = new Foo();
        unit.setId(RANDOM.nextInt());
        unit.setPayload(UUID.randomUUID().toString());
        amqpTemplate.convertAndSend(RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, RabbitConfiguration.POINT_KEY, unit, new CorrelationData("adf"));
        LOGGER.debug("produce: {}", unit);
        return ImmutableMap.of("code", 20000);
    }

    @RequestMapping(value = "/exchange-exists-but-queue-not", method = RequestMethod.GET)
    public Map<String, ?> exchangeExistsButQueueNot() {
        final Foo unit = new Foo();
        unit.setId(RANDOM.nextInt());
        unit.setPayload(UUID.randomUUID().toString());
        // 有这个exchange，但是无法投递至队列
        amqpTemplate.convertAndSend(RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, "sadf", unit, new CorrelationData("adf"));
        LOGGER.debug("produce: {}", unit);
        return ImmutableMap.of("code", 20000);
    }

    @RequestMapping(value = "/exchange-not-exist", method = RequestMethod.GET)
    public Map<String, ?> exchangeNotExist() {
        final Foo unit = new Foo();
        unit.setId(RANDOM.nextInt());
        unit.setPayload(UUID.randomUUID().toString());
        // 根本就没有这个exchange
        amqpTemplate.convertAndSend("sdflkjsldf", "sadf", unit, new CorrelationData("adf"));
        LOGGER.debug("produce: {}", unit);
        return ImmutableMap.of("code", 20000);
    }

    //@RabbitListener(queues = {RabbitConfiguration.POINT_QUEUE})
    public void processBootTask(Foo content) {
        LOGGER.debug("consume: {}", content);
    }

    @RequestMapping(value = "/bus/queue-exists", method = RequestMethod.GET)
    public Map<String, ?> queueExistEventBus() {
        eventBus.persistPublishMessage(ImmutableMap.of("hello", "java"), EventBusinessType.ADD_PTS.name());
        return ImmutableMap.of("code", 20000);
    }

    @RequestMapping(value = "/bus/exchange-exists-but-queue-not", method = RequestMethod.GET)
    public Map<String, ?> exchangeExistsButQueueNotEventBus() {
        EventDrivenPublisher.registerType("111", RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, "02394234");
        eventBus.persistPublishMessage(ImmutableMap.of("hello", "java"), "111");
        return ImmutableMap.of("code", 20000);
    }

    @RequestMapping(value = "/bus/exchange-not-exist", method = RequestMethod.GET)
    public Map<String, ?> exchangeNotExistEventBus() {
        EventDrivenPublisher.registerType("111", "129313", "02394234");
        eventBus.persistPublishMessage(ImmutableMap.of("hello", "java"), "111");
        return ImmutableMap.of("code", 20000);
    }

    @RabbitListener(queues = {RabbitConfiguration.POINT_QUEUE})
    public void processBootTaskBus(Map<String, Object> event) {
        //LOGGER.debug("consume: {}", event);
        subscriber.persistAndHandleMessage(event.get("business_type").toString(), event.get("payload").toString(), event.get("guid").toString());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
    private static class Foo {
        private Integer id;

        @JsonIgnore
        private String payload;

    }

}
