package com.agraph.common.types;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SequenceIdGenerator implements IdGenerator {

    private static final long EPOCH = 1483289999000L;
    private static final long WORKER_ID = IdGenerator.createWorkerIdentifier();
    private static final AtomicInteger COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final int TIMESTAMP_BITS = 41;
    private static final int WORKER_BITS = 10;
    private static final int COUNTER_BITS = 13;
    private static final int MAX_WORKER_ID = 1 << WORKER_BITS;
    private static final int MAX_COUNTER = 1 << COUNTER_BITS;
    private static final int WORKER_MASK = ~(-1 << WORKER_BITS);
    private static final int COUNTER_MASK = ~(-1 << COUNTER_BITS);

    @Override
    public long generate() {
        long timestamp = System.currentTimeMillis() - EPOCH;
        long workerId = ((WORKER_ID % MAX_WORKER_ID) & WORKER_MASK);
        long counter = (COUNTER.incrementAndGet() % MAX_COUNTER) & COUNTER_MASK;

        long id = timestamp << (64 - TIMESTAMP_BITS);
        id |= workerId << (64 - TIMESTAMP_BITS - WORKER_BITS);
        id |= counter;

        return id;
    }
}
