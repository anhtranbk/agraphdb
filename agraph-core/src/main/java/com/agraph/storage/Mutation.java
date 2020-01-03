package com.agraph.storage;

import java.nio.ByteBuffer;
import java.util.Map;

public interface Mutation {

    ByteBuffer getKey();

    Map<String, ByteBuffer> getProperties();

    boolean isEmpty();
}
