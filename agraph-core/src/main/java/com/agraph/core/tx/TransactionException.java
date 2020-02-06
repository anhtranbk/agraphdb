package com.agraph.core.tx;

import com.agraph.AGraphException;

public class TransactionException extends AGraphException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Object... args) {
        super(message, args);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
