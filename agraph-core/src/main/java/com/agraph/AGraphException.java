package com.agraph;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class AGraphException extends RuntimeException {

    public AGraphException() {
    }

    public AGraphException(String message) {
        super(message);
    }

    public AGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public AGraphException(Throwable cause) {
        super(cause);
    }
}
