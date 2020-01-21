package com.github.prontera.domain.type.handler;

import com.github.prontera.domain.type.TccStatus;
import com.github.prontera.persistence.GenericTypeHandler;

/**
 * @author Zhao Junjian
 */
public class TccStatusTypeHandler extends GenericTypeHandler<TccStatus> {
    @Override
    public int getEnumIntegerValue(TccStatus parameter) {
        return parameter.getStatus();
    }
}
