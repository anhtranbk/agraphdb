package com.agraph;

import com.agraph.common.util.Strings;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class AGraphException extends RuntimeException {

    private static final long serialVersionUID = -8711375282196157058L;

    public AGraphException(String message) {
        super(message);
    }

    public AGraphException(String message, Object... args) {
        super(String.format(message, args));
    }

    public AGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public AGraphException(String message, Throwable cause, Object... args) {
        super(Strings.format(message, args), cause);
    }

    public AGraphException(Throwable cause) {
        super(cause);
    }
}
