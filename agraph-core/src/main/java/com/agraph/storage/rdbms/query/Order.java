package com.agraph.storage.rdbms.query;

public interface Order {

    String column();

    boolean isAsc();

    static Order asc(String col) {
        return new Order() {
            @Override
            public String column() {
                return col;
            }

            @Override
            public boolean isAsc() {
                return true;
            }
        };
    }

    static Order desc(String col) {
        return new Order() {
            @Override
            public String column() {
                return col;
            }

            @Override
            public boolean isAsc() {
                return false;
            }
        };
    }
}
