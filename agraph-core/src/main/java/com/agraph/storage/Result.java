package com.agraph.storage;

import java.io.InputStream;
import java.io.Reader;
import java.util.Date;

public interface Result {

    boolean getBoolean(String col);

    byte getByte(String col);

    short getSmallInt(String col);

    int getInt(String col);

    long getBigInt(String col);

    float getFloat(String col);

    double getDouble(String col);

    byte[] getBytes(String col);

    String getString(String col);

    Date getDate(String col);

    long getTimestamp(String col);

    default InputStream getBlob(String col) {
        throw new UnsupportedOperationException();
    }

    default Reader getClob(String col) {
        throw new UnsupportedOperationException();
    }
}
