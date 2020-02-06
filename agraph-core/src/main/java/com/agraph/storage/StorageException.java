package com.agraph.storage;

import com.agraph.AGraphException;

public class StorageException extends AGraphException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Object... args) {
        super(message, args);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }
}
