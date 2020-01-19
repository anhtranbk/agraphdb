package com.agraph.exc;

import com.agraph.AGraphException;

public class GraphIoException extends AGraphException {

    public GraphIoException() {
    }

    public GraphIoException(String message) {
        super(message);
    }

    public GraphIoException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphIoException(Throwable cause) {
        super(cause);
    }
}
