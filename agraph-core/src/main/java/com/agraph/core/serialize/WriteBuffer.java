package com.agraph.core.serialize;

import java.nio.ByteBuffer;

public interface WriteBuffer {

    WriteBuffer putByte(byte val);

    WriteBuffer putBytes(byte[] val);

    WriteBuffer putBytes(ByteBuffer val);

    WriteBuffer putBoolean(boolean val);

    WriteBuffer putShort(short val);

    WriteBuffer putInt(int val);

    WriteBuffer putLong(long val);

    WriteBuffer putChar(char val);

    WriteBuffer putFloat(float val);

    WriteBuffer putDouble(double val);

    int getPosition();
}
