package com.agraph.common.tuple;

import java.util.Objects;

public class Tuple4<V1, V2, V3, V4> extends Tuple3<V1, V2, V3> {

    public final V4 _4;

    public Tuple4(V1 _1, V2 _2, V3 _3, V4 _4) {
        super(_1, _2, _3);
        this._4 = _4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;
        if (!super.equals(o)) return false;
        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(_4, tuple4._4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _4);
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "v1=" + this._1 +
                ", v2=" + this._2 +
                ", v3=" + this._3 +
                ", v4=" + _4 +
                '}';
    }
}
