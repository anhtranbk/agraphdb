package com.agraph.common.tuple;

import java.util.Objects;

public class Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {

    private final V3 v3;

    public Tuple3(V1 v1, V2 v2, V3 v3) {
        super(v1, v2);
        this.v3 = v3;
    }

    public V3 _3() {
        return this.v3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;
        if (!super.equals(o)) return false;
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(v3, tuple3.v3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), v3);
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "v1=" + this._1() +
                ", v2=" + this._2() +
                ", v3=" + v3 +
                '}';
    }
}
