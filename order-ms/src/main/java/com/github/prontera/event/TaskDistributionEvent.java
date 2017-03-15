package com.github.prontera.event;

import com.github.prontera.domain.EventProcess;
import org.springframework.context.ApplicationEvent;

/**
 * @author Zhao Junjian
 */
public class TaskDistributionEvent extends ApplicationEvent {
    private static final long serialVersionUID = -5611885147286505538L;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public TaskDistributionEvent(EventProcess source) {
        super(source);
    }
}
