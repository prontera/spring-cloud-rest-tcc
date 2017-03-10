package com.github.prontera.web.controller;

import com.github.prontera.config.RabbitConfiguration;
import com.github.prontera.config.WorkUnit;
import com.google.common.collect.ImmutableMap;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/rest", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class MessageController {
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public Map<String, ?> hello() {
        for (int i = 0; i < 1; i++) {
            final WorkUnit unit = new WorkUnit();
            unit.setId("1");
            unit.setMessage("hello world");
            // direct
            amqpTemplate.convertAndSend(RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, RabbitConfiguration.TRADE_ROUTE_KEY, unit);
            // fanout
            amqpTemplate.convertAndSend(RabbitConfiguration.DEFAULT_TOPIC_EXCHANGE, RabbitConfiguration.TOPIC_ROUTE_KEY, unit);
        }
        return ImmutableMap.of("code", 20000);
    }

    //@Scheduled(fixedDelay = 100)
    public void schedule() {
        final WorkUnit unit = new WorkUnit();
        unit.setId("1");
        unit.setMessage("hello world");
        amqpTemplate.convertAndSend(RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, RabbitConfiguration.TRADE_ROUTE_KEY, unit);
    }

}
