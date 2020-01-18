package com.agraph.storage.rdbms.query;

import com.agraph.common.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MultiCondition implements Condition {

    private final Operator operator;
    private final List<Condition> conditions;

    MultiCondition(Operator operator, List<Condition> conditions) {
        this.operator = operator;
        this.conditions = conditions;
    }

    public List<Condition> conditions() {
        return Collections.unmodifiableList(conditions);
    }

    @Override
    public Operator operator() {
        return operator;
    }

    @Override
    public boolean isMulti() {
        return true;
    }

    @Override
    public String asString() {
        final String sep = " " + operator.symbol + " ";
        return "(" + Strings.join(conditions, sep) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiCondition)) return false;
        MultiCondition that = (MultiCondition) o;
        return conditions.equals(that.conditions) && operator.equals(that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, conditions);
    }

    @Override
    public String toString() {
        return asString();
    }
}
