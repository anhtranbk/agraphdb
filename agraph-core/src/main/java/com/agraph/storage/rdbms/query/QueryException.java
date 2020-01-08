package com.agraph.storage.rdbms.query;

import com.agraph.storage.StorageException;

public class QueryException extends StorageException {

    public QueryException() {
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }
}
