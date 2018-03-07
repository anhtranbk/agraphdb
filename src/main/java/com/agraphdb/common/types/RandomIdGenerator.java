package com.agraphdb.common.types;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class RandomIdGenerator implements IdGenerator {

    private static final long EPOCH = 1483289999000L;
    private static final long WORKER_ID = IdGenerator.createWorkerIdentifier();
    private static final AtomicInteger COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final int WORKER_BITS = 10;
    private static final int COUNTER_BITS = 12;
    private static final int MAX_WORKER_ID = 1 << WORKER_BITS;
    private static final int MAX_COUNTER = 1 << COUNTER_BITS;
    private static final int WORKER_MASK = ~(-1 << WORKER_BITS);
    private static final int COUNTER_MASK = ~(-1 << COUNTER_BITS);

    @Override
    public long generate() {
        long timestamp = System.currentTimeMillis() - EPOCH;
        long workerId = ((WORKER_ID % MAX_WORKER_ID) & WORKER_MASK);
        long counter = (COUNTER.incrementAndGet() % MAX_COUNTER) & COUNTER_MASK;

        // Mặc dù số bit đủ để lưu trữ counter là 12 (range 0-4095), nhưng do
        // counter chiếm các bit đầu tiên nên để bảo đảm số int64 sinh ra là
        // số dương thì số bit thực tế cần cho counter là 13
        long id = counter << (64 - COUNTER_BITS - 1);
        id |= workerId << (64 - COUNTER_BITS - WORKER_BITS - 1);
        id |= timestamp;

        return id;
    }
}
