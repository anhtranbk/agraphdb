package com.agraph.storage;

import com.agraph.storage.backend.BackendSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe class to manage Backend sessions
 * @param <T> type of Backend Session class
 */
public abstract class AbstractStorageBackend<T extends BackendSession> implements StorageBackend {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ThreadLocal<T> threadLocalSession = new ThreadLocal<>();
    private final AtomicInteger sessionCount = new AtomicInteger(0);
    private final Map<Long, T> sessions = new ConcurrentHashMap<>();

    public T session() {
        T session = this.threadLocalSession.get();
        if (session == null) {
            session = newSession();
            this.threadLocalSession.set(session);

            long tId = Thread.currentThread().getId();
            this.sessions.put(tId, session);

            int sc = this.sessionCount.incrementAndGet();
            logger.info("Created backend session at Thread[{}], session count: {}", tId, sc);
        }
        return session;
    }

    public int sessionCount() {
        return this.sessionCount.get();
    }

    /**
     * Close session associate with current thread
     */
    public int closeSession() {
        T session = threadLocalSession.get();
        if (session == null) {
            logger.warn("Current session has ever been closed");
            return this.sessionCount.get();
        }

        this.closeSession(Thread.currentThread().getId(), session);
        this.threadLocalSession.remove();
        return this.sessionCount.get();
    }

    @Override
    public void close() {
        logger.debug("Close all {} backend sessions", sessionCount.get());
        for (Map.Entry<Long, T> entry : this.sessions.entrySet()) {
            this.closeSession(entry.getKey(), entry.getValue());
        }
        logger.info("All backend sessions has been closed");
    }

    protected void closeSession(long tId, T session) {
        logger.debug("Close backend session at Thread[{}]", tId);
        this.doCloseSession(session);
        this.sessions.remove(Thread.currentThread().getId());
        this.sessionCount.decrementAndGet();

        int sc = this.sessionCount.decrementAndGet();
        logger.info("Closed backend session at Thread[{}], session count: {}", tId, sc);
    }

    protected abstract T newSession();

    protected void doCloseSession(T session) {
        session.close();
    }
}
