package com.github.prontera.config;

import com.github.prontera.event.handler.AllAllowEventProcessHandler;
import com.github.prontera.event.handler.EventProcessHandler;
import com.github.prontera.event.handler.NoOpsEventProcessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhao Junjian
 */
@Configuration
public class BeanConfiguration {
    @Bean
    public EventProcessHandler handlerChain() {
        final AllAllowEventProcessHandler head = new AllAllowEventProcessHandler();
        final NoOpsEventProcessHandler left = new NoOpsEventProcessHandler();
        head.setSuccessor(left);
        return head;
    }
}
