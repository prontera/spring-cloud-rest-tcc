package com.github.prontera.model.event;

import com.github.prontera.config.RabbitConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zhao Junjian
 */
public enum RouteMapping {
    PLACE_ORDER(RabbitConfiguration.DEFAULT_DIRECT_EXCHANGE, RabbitConfiguration.TRADE_REQUEST_ROUTE_KEY);

    private final String exchange;

    private final String routeKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteMapping.class);

    RouteMapping(String exchange, String routeKey) {
        this.exchange = exchange;
        this.routeKey = routeKey;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public static RouteMapping parse(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            LOGGER.error("{} does not predefine. {}", name, e);
        }
        return null;
    }
}
