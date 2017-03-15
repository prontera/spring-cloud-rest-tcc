package com.github.prontera.model.type.handler;

import com.github.prontera.common.model.typehandler.GenericTypeHandler;
import com.github.prontera.model.type.OrderStatus;

/**
 * @author Zhao Junjian
 */
public class OrderStatusHandler extends GenericTypeHandler<OrderStatus> {

    @Override
    public int getEnumIntegerValue(OrderStatus parameter) {
        return parameter.getCode();
    }
}
