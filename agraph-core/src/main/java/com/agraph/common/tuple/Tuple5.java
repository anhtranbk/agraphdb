package com.agraph.common.tuple;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Tuple5<V1, V2, V3, V4, V5> extends Tuple4<V1, V2, V3, V4> {

    private final V5 v5;

    public Tuple5(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        super(v1, v2, v3, v4);
        this.v5 = v5;
    }

    public V5 _5() {
        return this.v5;
    }
}
