package com.agraph.storage.rdbms.query;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public class Order {

    @Accessors(fluent = true)
    private final String column;

    private final boolean asc;

    public Order(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public static Order asc(String col) {
        return new Order(col, true);
    }

    public static Order desc(String col) {
        return new Order(col, false);
    }
}
