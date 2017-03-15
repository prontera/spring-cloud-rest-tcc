package com.github.prontera.config;

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
    public static final int PAYLOAD_MAXIMUM_LENGTH = 1024;

    public static final String DEFAULT_DIRECT_EXCHANGE = "prontera.direct";

    public static final String TRADE_REQUEST_QUEUE = "funds-request";
    public static final String TRADE_REQUEST_DEAD_QUEUE = TRADE_REQUEST_QUEUE + "-dead-letter";
    public static final String TRADE_REQUEST_ROUTE_KEY = "account-ms";
    public static final String TRADE_REQUEST_DEAD_ROUTE_KEY = TRADE_REQUEST_ROUTE_KEY + "-dead";

    public static final String TRADE_RESPONSE_QUEUE = "funds-response";
    public static final String TRADE_RESPONSE_DEAD_QUEUE = TRADE_RESPONSE_QUEUE + "-dead-letter";
    public static final String TRADE_RESPONSE_ROUTE_KEY = "account-ms";
    public static final String TRADE_RESPONSE_DEAD_ROUTE_KEY = TRADE_RESPONSE_ROUTE_KEY + "-dead";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DEFAULT_DIRECT_EXCHANGE, true, true);
    }

    @Bean
    public Queue tradeQueue() {
        final ImmutableMap<String, Object> args = ImmutableMap.of("x-dead-letter-exchange", DEFAULT_DIRECT_EXCHANGE,
                "x-dead-letter-routing-key", TRADE_REQUEST_DEAD_ROUTE_KEY);
        return new Queue(TRADE_REQUEST_QUEUE, true, false, true, args);
    }

    @Bean
    public Binding tradeBinding() {
        return BindingBuilder.bind(tradeQueue()).to(directExchange()).with(TRADE_REQUEST_ROUTE_KEY);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(TRADE_REQUEST_DEAD_QUEUE, true, false, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(directExchange()).with(TRADE_REQUEST_DEAD_ROUTE_KEY);
    }

}
