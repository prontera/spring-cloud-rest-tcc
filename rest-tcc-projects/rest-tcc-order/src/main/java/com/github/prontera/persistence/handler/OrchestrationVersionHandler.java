package com.github.prontera.persistence.handler;

import com.github.prontera.enums.OrchestrationVersion;
import com.github.prontera.persistence.GenericTypeHandler;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public class OrchestrationVersionHandler extends GenericTypeHandler<OrchestrationVersion> {

    @Override
    public int getEnumIntegerValue(OrchestrationVersion parameter) {
        return parameter.val();
    }

}
