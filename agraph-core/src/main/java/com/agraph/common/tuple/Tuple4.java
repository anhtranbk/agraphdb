package com.agraph.common.tuple;

import java.util.Objects;

public class Tuple4<V1, V2, V3, V4> extends Tuple3<V1, V2, V3> {

    private final V4 v4;

    public Tuple4(V1 v1, V2 v2, V3 v3, V4 v4) {
        super(v1, v2, v3);
        this.v4 = v4;
    }

    public V4 _4() {
        return this.v4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;
        if (!super.equals(o)) return false;
        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(v4, tuple4.v4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), v4);
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "v1=" + this._1() +
                ", v2=" + this._2() +
                ", v3=" + this._3() +
                ", v4=" + v4 +
                '}';
    }
}
