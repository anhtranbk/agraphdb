package com.agraph.core.idpool;

import com.google.common.base.Preconditions;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ShardedIdPool implements IdPool {

    private static final long EPOCH = 1483289999000L;
    private static final long WORKER_ID = IdPool.createWorkerIdentifier();
    private static final AtomicInteger COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final int WORKER_BITS = 10;
    private static final int COUNTER_BITS = 12;
    private static final int MAX_WORKER_ID = 1 << WORKER_BITS;
    private static final int MAX_COUNTER = 1 << COUNTER_BITS;
    private static final int WORKER_MASK = ~(-1 << WORKER_BITS);
    private static final int COUNTER_MASK = ~(-1 << COUNTER_BITS);

    private final int shards;

    public ShardedIdPool(int shards) {
        Preconditions.checkArgument(shards >= 1 && shards < 128,
                "Number shards must be between [1, 127]");
        this.shards = shards;
    }

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
