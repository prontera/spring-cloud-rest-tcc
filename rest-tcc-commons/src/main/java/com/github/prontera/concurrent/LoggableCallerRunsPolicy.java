package com.github.prontera.concurrent;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Zhao Junjian
 * @date 2020/01/29
 */
public class LoggableCallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy implements LoggablePolicy {
    private final String recordName;

    public LoggableCallerRunsPolicy(String recordName) {
        this.recordName = Objects.requireNonNull(recordName, "recordName");
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        // report here
        super.rejectedExecution(r, e);
    }

    @Override
    public String getRecordName() {
        return recordName;
    }

}
