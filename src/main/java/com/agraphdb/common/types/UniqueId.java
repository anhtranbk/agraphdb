package com.agraphdb.common.types;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface UniqueId {

    long timestamp();

    long asLong();

    String asHexString();
}
