package com.github.prontera.model.type.handler;

import com.github.prontera.domain.type.handler.GenericTypeHandler;

/**
 * @author Zhao Junjian
 */
public class OrderStatusHandler extends GenericTypeHandler<OrderStatus> {

    @Override
    public int getEnumIntegerValue(OrderStatus parameter) {
        return parameter.getCode();
    }
}
