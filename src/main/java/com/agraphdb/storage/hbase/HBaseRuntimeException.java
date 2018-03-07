package com.agraphdb.storage.hbase;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HBaseRuntimeException extends RuntimeException {

    public HBaseRuntimeException() {
    }

    public HBaseRuntimeException(String message) {
        super(message);
    }

    public HBaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HBaseRuntimeException(Throwable cause) {
        super(cause);
    }
}
