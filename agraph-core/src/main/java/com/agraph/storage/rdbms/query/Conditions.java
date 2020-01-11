package com.agraph.storage.rdbms.query;

import java.util.Arrays;

public class Conditions {

    public static SingleCondition eq(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.EQUALS, obj);
    }

    public static SingleCondition neq(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.NOT_EQUALS, obj);
    }

    public static SingleCondition lt(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.LESS, obj);
    }

    public static SingleCondition lte(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.LESS_OR_EQUALS, obj);
    }

    public static SingleCondition gt(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.GREATER, obj);
    }

    public static SingleCondition gte(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.GREATER_OR_EQUALS, obj);
    }

    public static SingleCondition isNull(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.IS_NULL, obj);
    }

    public static SingleCondition notNull(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.IS_NOT_NULL, obj);
    }

    public static SingleCondition in(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.IN, obj);
    }

    public static SingleCondition notIn(String col, Object obj) {
        return new SingleCondition(col, Condition.Operator.NOT_IN, obj);
    }

    public static MultiCondition or(Condition... conditions) {
        return new MultiCondition(Condition.Operator.OR, Arrays.asList(conditions));
    }

    public static MultiCondition and(Condition... conditions) {
        return new MultiCondition(Condition.Operator.AND, Arrays.asList(conditions));
    }
}
