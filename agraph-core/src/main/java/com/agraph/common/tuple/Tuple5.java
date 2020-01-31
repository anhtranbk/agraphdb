package com.agraph.common.tuple;

import java.util.Objects;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Tuple5<V1, V2, V3, V4, V5> extends Tuple4<V1, V2, V3, V4> {

    public final V5 _5;

    public Tuple5(V1 _1, V2 _2, V3 _3, V4 _4, V5 _5) {
        super(_1, _2, _3, _4);
        this._5 = _5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple5)) return false;
        if (!super.equals(o)) return false;
        Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(_5, tuple5._5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _5);
    }

    @Override
    public String toString() {
        return "Tuple5{" +
                "v1=" + this._1 +
                ", v2=" + this._2 +
                ", v3=" + this._3 +
                ", v4=" + this._4 +
                ", v5=" + _5 +
                '}';
    }
}
