package com.agraph.common.tuple;

public class Tuple4<V1, V2, V3, V4> extends Tuple3<V1, V2, V3> {

    private final V4 v4;

    public Tuple4(V1 v1, V2 v2, V3 v3, V4 v4) {
        super(v1, v2, v3);
        this.v4 = v4;
    }

    public V4 _4() {
        return this.v4;
    }
}
