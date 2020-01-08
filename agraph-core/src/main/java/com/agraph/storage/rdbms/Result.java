package com.agraph.storage.rdbms;

import java.nio.ByteBuffer;

public interface Result {

    String getString(int pos);

    boolean getBoolean(int pos);

    byte getByte(int pos);

    short getShort(int pos);

    int getInt(int pos);

    long getLong(int pos);

    float getFloat(int pos);

    double getDouble(int pos);

    byte[] getBytes(int pos);
    
    ByteBuffer getByteBuffer(int pos);

    String getString(String col);

    boolean getBoolean(String col);

    byte getByte(String col);

    short getShort(String col);

    int getInt(String col);

    long getLong(String col);

    float getFloat(String col);

    double getDouble(String col);

    byte[] getBytes(String col);

    ByteBuffer getByteBuffer(String col);
}
