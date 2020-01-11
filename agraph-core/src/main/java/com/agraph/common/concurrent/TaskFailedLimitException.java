package com.agraph.common.concurrent;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TaskFailedLimitException extends RuntimeException {

    public TaskFailedLimitException() {
    }

    public TaskFailedLimitException(String message) {
        super(message);
    }

    public TaskFailedLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskFailedLimitException(Throwable cause) {
        super(cause);
    }
}
