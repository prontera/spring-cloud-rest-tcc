package com.github.prontera.config;

import com.github.prontera.EventDrivenPublisher;
import com.google.common.collect.ImmutableMap;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhao Junjian
 */
@Configuration
public class RabbitConfiguration {

    public static final String DEFAULT_DIRECT_EXCHANGE = "prontera.direct";
    public static final String POINT_QUEUE = "point";
    public static final String DEAD_POINT_QUEUE = "d.point";
    public static final String POINT_KEY = "0666fb88-4cc2-11e7-9226-0242ac130004";
    public static final String DEAD_POINT_KEY = "a0b1d08b-4ccd-11e7-9226-0242ac130004";

    static {
        EventDrivenPublisher.registerType(EventBusinessType.ADD_PTS.name(), DEFAULT_DIRECT_EXCHANGE, POINT_KEY);
    }

    @Bean
    public EventDrivenPublisher eventDrivenPublisher() {
        return new EventDrivenPublisher();
    }

    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(DEFAULT_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue pointQueue() {
        final ImmutableMap<String, Object> args =
                ImmutableMap.of("x-dead-letter-exchange", DEFAULT_DIRECT_EXCHANGE,
                        "x-dead-letter-routing-key", DEAD_POINT_KEY);
        return new Queue(POINT_QUEUE, true, false, false, args);
    }

    @Bean
    public Binding pointBinding() {
        return BindingBuilder.bind(pointQueue()).to(defaultExchange()).with(POINT_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue deafPointQueue() {
        return new Queue(DEAD_POINT_QUEUE, true, false, false);
    }

    @Bean
    public Binding deadPointBinding() {
        return BindingBuilder.bind(deafPointQueue()).to(defaultExchange()).with(DEAD_POINT_KEY);
    }

}
