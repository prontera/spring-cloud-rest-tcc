package com.github.prontera.security.session.listener;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Solar
 */
@Component
public class SessionListener extends SessionListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void onStart(Session session) {
        LOGGER.debug(" ===>> Session: [{}] From: [{}] CREATE ", session.getId(), session.getHost());
    }

    @Override
    public void onExpiration(Session session) {
        LOGGER.debug(" <<=== Session: [{}] From: [{}] EXPIRE ", session.getId(), session.getHost());
    }

    @Override
    public void onStop(Session session) {
        LOGGER.debug(" <<=== Session: [{}] From: [{}] STOP ", session.getId(), session.getHost());
    }
}
