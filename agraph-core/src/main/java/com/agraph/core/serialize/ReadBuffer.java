package com.agraph.core.serialize;

public interface ReadBuffer {

    boolean hasRemaining();

    byte getByte();

    boolean getBoolean();

    short getShort();

    int getInt();

    long getLong();

    char getChar();

    float getFloat();

    double getDouble();

    byte[] getBytes(int length);

    short[] getShorts(int length);

    int[] getInts(int length);

    long[] getLongs(int length);

    char[] getChars(int length);

    float[] getFloats(int length);

    double[] getDoubles(int length);
}
