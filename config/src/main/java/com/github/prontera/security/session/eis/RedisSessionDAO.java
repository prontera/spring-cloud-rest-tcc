package com.github.prontera.security.session.eis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author Solar
 */
public class RedisSessionDAO extends AbstractSessionDAO {
    private static final String ACTIVE_SESSION = "atv:session:";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionDAO.class);
    private RedisTemplate<Serializable, Session> redisTemplate;
    private ValueOperations<Serializable, Session> sessionOperations;

    public RedisSessionDAO(RedisTemplate<Serializable, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
        sessionOperations = redisTemplate.opsForValue();
    }

    @Override
    protected Serializable doCreate(Session session) {
        final Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        sessionOperations.set(sessionId, session, 20, TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        final Session session = sessionOperations.get(sessionId);
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        sessionOperations.set(session.getId(), session, 20, TimeUnit.SECONDS);
    }

    @Override
    public void delete(Session session) {
        final Serializable sessionId = session.getId();
        redisTemplate.delete(sessionId);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return Collections.emptySet();
    }
}
