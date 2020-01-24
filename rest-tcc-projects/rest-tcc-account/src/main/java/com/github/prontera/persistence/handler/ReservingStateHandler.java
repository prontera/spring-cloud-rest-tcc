package com.github.prontera.persistence.handler;

import com.github.prontera.account.enums.ReservingState;
import com.github.prontera.persistence.GenericTypeHandler;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public class ReservingStateHandler extends GenericTypeHandler<ReservingState> {

    @Override
    public int getEnumIntegerValue(ReservingState parameter) {
        return parameter.val();
    }

}
