package com.agraph.common.tuple;

public class Tuple2<V1, V2> {

    private final V1 v1;
    private final V2 v2;

    public Tuple2(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 _1() {
        return this.v1;
    }

    public V2 _2() {
        return this.v2;
    }
}
