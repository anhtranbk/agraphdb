package com.agraphdb.common.lifecycle;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface LifeCycle {

    int STARTING = 1;
    int STARTED = 2;
    int STOPPING = 3;
    int STOPPED = -1;

    void start();

    void stop();

    int state();

    boolean isRunning();

    boolean isStarted();

    boolean isStarting();

    boolean isStopping();

    boolean isStopped();
}
