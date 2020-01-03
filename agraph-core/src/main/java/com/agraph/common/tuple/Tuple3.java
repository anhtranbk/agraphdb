package com.agraph.common.tuple;

public class Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {

    private final V3 v3;

    public Tuple3(V1 v1, V2 v2, V3 v3) {
        super(v1, v2);
        this.v3 = v3;
    }

    public V3 _3() {
        return this.v3;
    }
}
