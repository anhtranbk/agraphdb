package com.agraph.storage.rdbms.query;

import java.util.Objects;

public class SingleCondition implements Condition {

    private final String column;
    private final Operator operator;
    private final Object args;

    SingleCondition(String col, Operator operator, Object args) {
        this.column = col;
        this.operator = operator;
        this.args = args;
    }

    public String column() {
        return this.column;
    }

    public Object arguments() {
        return args;
    }

    @Override
    public Operator operator() {
        return this.operator;
    }

    @Override
    public boolean isMulti() {
        return false;
    }

    @Override
    public String asString() {
        if (operator == Operator.RANGE) {
            return column + " " + operator.symbol + " %s AND %s";
        } else {
            return column + " " + operator.symbol + " %s";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleCondition)) return false;
        SingleCondition that = (SingleCondition) o;
        return column.equals(that.column) &&
                operator == that.operator &&
                args.equals(that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, operator, args);
    }

    @Override
    public String toString() {
        return asString();
    }
}
