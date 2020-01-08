package com.agraph.core.cache;

import java.nio.ByteBuffer;

public interface Cacheable {

    long id();

    ByteBuffer data();
}
