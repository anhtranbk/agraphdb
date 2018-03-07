package com.vcc.bigdata.common.tasks;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TaskErrorExceedLimitException extends RuntimeException {

    public TaskErrorExceedLimitException() {
    }

    public TaskErrorExceedLimitException(String message) {
        super(message);
    }

    public TaskErrorExceedLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskErrorExceedLimitException(Throwable cause) {
        super(cause);
    }
}
