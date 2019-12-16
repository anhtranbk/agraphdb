package com.agraph.common.lifecycle;

import com.agraph.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile int state = STOPPED;
    private final Object _lock = new Object();
    private final AtomicBoolean flagStop = new AtomicBoolean(false);

    public AbstractLifeCycle() {
        Utils.addShutdownHook(this::stop);
    }

    protected void onInitialize() {
    }

    protected void onStart() {
    }

    protected void onProcess() {
    }

    protected void onStop() {
    }

    public final boolean isCanceled() {
        return this.flagStop.get();
    }

    public final boolean isNotCanceled() {
        return !isCanceled();
    }

    @Override
    public final void start() {
        if (!this.setStarting()) return;

        logger.info("Life cycle initializing...");
        this.onInitialize();

        logger.info("Life cycle starting...");
        this.onStart();

        logger.info("Life cycle started...");
        this.setStarted();
        this.onProcess();
    }

    @Override
    public final void stop() {
        if (!this.setStopping()) return;

        logger.info("Life cycle stopping...");
        this.flagStop.set(true);
        this.onStop();

        logger.info("Life cycle stopped...");
        this.setStopped();
    }

    private boolean setStarting() {
        synchronized (_lock) {
            if (state == STARTING || state == STARTED) return false;
            this.state = STARTING;
        }
        return true;
    }

    private boolean setStarted() {
        synchronized (_lock) {
            if (state == STARTING || state == STARTED) return false;
            this.state = STARTED;
        }
        return true;
    }

    private boolean setStopping() {
        synchronized (_lock) {
            if (state == STOPPED || state == STOPPING) return false;
            this.state = STOPPING;
        }
        return true;
    }

    private boolean setStopped() {
        synchronized (_lock) {
            if (state == STOPPED || state == STOPPING) return false;
            this.state = STOPPED;
        }
        return true;
    }

    @Override
    public final int state() {
        return this.state;
    }

    @Override
    public final boolean isRunning() {
        return this.state == STARTING || this.state == STARTED;
    }

    @Override
    public final boolean isStarted() {
        return this.state == STARTED;
    }

    @Override
    public final boolean isStarting() {
        return this.state == STARTING;
    }

    @Override
    public final boolean isStopping() {
        return this.state == STOPPING;
    }

    @Override
    public final boolean isStopped() {
        return this.state == STOPPED;
    }
}
