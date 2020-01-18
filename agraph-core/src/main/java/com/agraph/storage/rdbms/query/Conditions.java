package com.agraph.storage.rdbms.query;

import com.agraph.common.tuple.Tuple2;

import java.util.Arrays;

import static com.agraph.storage.rdbms.query.Condition.Operator;

public class Conditions {

    public static SingleCondition eq(String col, Object obj) {
        return new SingleCondition(col, Operator.EQUALS, obj);
    }

    public static SingleCondition neq(String col, Object obj) {
        return new SingleCondition(col, Operator.NOT_EQUALS, obj);
    }

    public static SingleCondition lt(String col, Object obj) {
        return new SingleCondition(col, Operator.LESS, obj);
    }

    public static SingleCondition lte(String col, Object obj) {
        return new SingleCondition(col, Operator.LESS_OR_EQUALS, obj);
    }

    public static SingleCondition gt(String col, Object obj) {
        return new SingleCondition(col, Operator.GREATER, obj);
    }

    public static SingleCondition gte(String col, Object obj) {
        return new SingleCondition(col, Operator.GREATER_OR_EQUALS, obj);
    }

    public static SingleCondition isNull(String col, Object obj) {
        return new SingleCondition(col, Operator.IS_NULL, obj);
    }

    public static SingleCondition notNull(String col, Object obj) {
        return new SingleCondition(col, Operator.IS_NOT_NULL, obj);
    }

    public static SingleCondition range(String col, Number obj1, Number obj2) {
        return new SingleCondition(col, Operator.RANGE, new Tuple2<>(obj1, obj2));
    }

    public static SingleCondition in(String col, Object obj) {
        return new SingleCondition(col, Operator.IN, obj);
    }

    public static SingleCondition notIn(String col, Object obj) {
        return new SingleCondition(col, Operator.NOT_IN, obj);
    }

    public static MultiCondition or(Condition... conditions) {
        return new MultiCondition(Operator.OR, Arrays.asList(conditions));
    }

    public static MultiCondition and(Condition... conditions) {
        return new MultiCondition(Operator.AND, Arrays.asList(conditions));
    }
}
