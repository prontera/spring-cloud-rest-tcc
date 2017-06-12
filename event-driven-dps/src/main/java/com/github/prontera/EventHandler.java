package com.github.prontera;

import com.github.prontera.domain.EventSubscriber;

/**
 * @author Zhao Junjian
 */
public interface EventHandler {
    void handler(EventSubscriber subscriber);
}
