package com.agraph.core.tx;

import java.time.Duration;

public interface TransactionLock {

    void lock(Duration timeout);

    void unlock();

    boolean inUse();
}
