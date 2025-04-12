package com.hrms.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisSessionDao extends CachingSessionDAO {

    final String SESSION_PREFIX = "shiro:session:";

    RedisTemplate<Serializable, Session> redisTemplate;

    public RedisSessionDao(RedisTemplate<Serializable, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doUpdate(Session session) {
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
        updateRedis(session);
    }

    @Override
    protected void doDelete(Session session) {
        redisTemplate.delete(SESSION_PREFIX + session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        assignSessionId(session, generateSessionId(session));
        updateRedis(session);
        return session.getId();
    }

    private void updateRedis(Session session) {
        long timeout = session.getTimeout();
        if (timeout <= 0) {
            timeout = TimeUnit.HOURS.toMillis(1);
        }
        redisTemplate.opsForValue().set(SESSION_PREFIX + session.getId(), session, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
    }
}
