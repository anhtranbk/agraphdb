package com.agraph.internal;

public interface InternalElement {

    byte STATE_EMPTY = 0;
    byte STATE_NEW = 1;
    byte STATE_LOADED = 2;
    byte STATE_MODIFIED = 4;
    byte STATE_REMOVED = 8;
}
