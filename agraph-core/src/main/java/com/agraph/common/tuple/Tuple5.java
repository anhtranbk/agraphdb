package com.agraph.common.tuple;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple5)) return false;
        if (!super.equals(o)) return false;
        Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(v5, tuple5.v5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), v5);
    }

    @Override
    public String toString() {
        return "Tuple5{" +
                "v1=" + this._1() +
                ", v2=" + this._2() +
                ", v3=" + this._3() +
                ", v4=" + this._4() +
                ", v5=" + v5 +
                '}';
    }
}
