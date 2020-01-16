package com.github.prontera.event;

import com.github.prontera.domain.UserBalanceTcc;
import org.springframework.context.ApplicationEvent;

/**
 * @author Zhao Junjian
 */
public class ReservedBalanceCancellationEvent extends ApplicationEvent {
    private static final long serialVersionUID = -3561050469176976072L;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ReservedBalanceCancellationEvent(UserBalanceTcc source) {
        super(source);
    }
}
