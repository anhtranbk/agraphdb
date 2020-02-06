package com.agraph.core.serialize;

import com.agraph.AGraphException;

public class SerializationException extends AGraphException {

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Object... args) {
        super(message, args);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
