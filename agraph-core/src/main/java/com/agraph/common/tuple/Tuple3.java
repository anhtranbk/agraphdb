package com.agraph.common.tuple;

import java.util.Objects;

public class Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {

    public final V3 _3;

    public Tuple3(V1 _1, V2 _2, V3 _3) {
        super(_1, _2);
        this._3 = _3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;
        if (!super.equals(o)) return false;
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(_3, tuple3._3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _3);
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "v1=" + this._1 +
                ", v2=" + this._2 +
                ", v3=" + _3 +
                '}';
    }
}
