package com.agraph.core;

import java.nio.ByteBuffer;

public interface ElementId extends Comparable<ElementId> {

    int MAX_LENGTH = 33;

    String asString();

    ByteBuffer asBytes();

    int length();

    IdType type();

    enum IdType {
        UNKNOWN,
        INTEGER,
        LONG,
        UUID,
        STRING,
        EDGE;
    }
}
