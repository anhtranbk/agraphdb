package com.agraph.core.internal;

import java.nio.ByteBuffer;

public interface ElementId {

    ByteBuffer toBytes();

    int getLength();
}
