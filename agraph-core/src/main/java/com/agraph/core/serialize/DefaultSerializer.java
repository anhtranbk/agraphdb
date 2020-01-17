package com.agraph.core.serialize;

import com.agraph.core.type.DataType;
import com.agraph.core.type.Types;

public class DefaultSerializer implements Serializer {

    @Override
    public byte[] write(DataType type, Object obj) {
        return Types.encode(type, obj);
    }

    @Override
    public Object read(byte[] serialized) {
        return Types.decode(serialized)._2();
    }
}
