package com.github.prontera.concurrent;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Zhao Junjian
 * @date 2020/01/29
 */
public class LoggableDiscardPolicy extends ThreadPoolExecutor.DiscardPolicy implements LoggablePolicy {
    private final String recordName;

    public LoggableDiscardPolicy(String recordName) {
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
