package com.agraph.internal;

import java.nio.ByteBuffer;

public interface ElementId {

    ByteBuffer toBytes();

    int getLength();
}
