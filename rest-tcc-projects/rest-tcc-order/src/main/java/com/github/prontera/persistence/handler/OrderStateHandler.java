package com.github.prontera.persistence.handler;

import com.github.prontera.enums.OrderState;
import com.github.prontera.persistence.GenericTypeHandler;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public class OrderStateHandler extends GenericTypeHandler<OrderState> {

    @Override
    public int getEnumIntegerValue(OrderState parameter) {
        return parameter.val();
    }

}
