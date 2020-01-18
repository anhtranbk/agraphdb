package com.agraph.storage.rdbms.query;

public interface Condition {

    enum Operator {
        EQUALS("="),
        NOT_EQUALS("!="),
        LESS("<"),
        LESS_OR_EQUALS("<="),
        GREATER(">"),
        GREATER_OR_EQUALS(">="),
        IN("IN"),
        NOT_IN("NOT IN"),
        RANGE("BETWEEN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        OR("OR"),
        AND("AND");

        public final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }
    }

    Operator operator();

    boolean isMulti();

    String asString();
}
