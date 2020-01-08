package com.agraph.common.lifecycle;

import com.agraph.common.util.Threads;
import com.agraph.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class LoopableLifeCycle extends AbstractLifeCycle {

    private long sleepAfterDone;
    private long sleepAfterFail;

    public LoopableLifeCycle(long sleepAfterDone, long sleepAfterFail) {
        setSleepTime(sleepAfterDone, sleepAfterFail);
    }

    public LoopableLifeCycle() {
        setSleepTime(60, 30);
    }

    public LoopableLifeCycle(Config conf) {
        setSleepTime(conf);
    }

    @Override
    protected final void onProcess() {
        boolean success;
        while (!isCanceled()) {
            try {
                this.onLoop();
                success = true;
            } catch (Throwable t) {
                success = false;
                this.logger.error(t.getMessage(), t);
            }

            Threads.sleep(success ? sleepAfterDone : sleepAfterFail, TimeUnit.SECONDS);
        }
    }

    protected void setSleepTime(long sleepAfterDone, long sleepAfterFail) {
        this.sleepAfterDone = sleepAfterDone;
        this.sleepAfterFail = sleepAfterFail;
    }

    protected void setSleepTime(Config conf) {
        this.sleepAfterDone = conf.getLong("lifecycle.loop.done.sleep.s", 60);
        this.sleepAfterFail = conf.getLong("lifecycle.loop.fail.sleep.s", 30);
    }

    protected abstract void onLoop() throws Exception;
}
