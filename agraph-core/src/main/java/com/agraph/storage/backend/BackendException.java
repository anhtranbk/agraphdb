package com.agraph.storage.backend;

import com.agraph.AGraphException;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class BackendException extends AGraphException {

    public BackendException() {
    }

    public BackendException(String message) {
        super(message);
    }

    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackendException(Throwable cause) {
        super(cause);
    }
}
