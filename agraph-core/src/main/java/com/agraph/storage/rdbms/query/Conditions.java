package com.agraph.storage.rdbms.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Conditions {

    public static SingleCondition eq(String col, Object obj) {
        return new Single(col, Condition.Operator.EQUALS, obj);
    }

    public static SingleCondition neq(String col, Object obj) {
        return new Single(col, Condition.Operator.NOT_EQUALS, obj);
    }

    public static SingleCondition lt(String col, Object obj) {
        return new Single(col, Condition.Operator.LESS, obj);
    }

    public static SingleCondition lte(String col, Object obj) {
        return new Single(col, Condition.Operator.LESS_OR_EQUALS, obj);
    }

    public static SingleCondition gt(String col, Object obj) {
        return new Single(col, Condition.Operator.GREATER, obj);
    }

    public static SingleCondition gte(String col, Object obj) {
        return new Single(col, Condition.Operator.GREATER_OR_EQUALS, obj);
    }

    public static SingleCondition isNull(String col, Object obj) {
        return new Single(col, Condition.Operator.IS_NULL, obj);
    }

    public static SingleCondition notNull(String col, Object obj) {
        return new Single(col, Condition.Operator.IS_NOT_NULL, obj);
    }

    public static SingleCondition in(String col, Object obj) {
        return new Single(col, Condition.Operator.IN, obj);
    }

    public static SingleCondition notIn(String col, Object obj) {
        return new Single(col, Condition.Operator.NOT_IN, obj);
    }

    public static MultiCondition or(Condition... conditions) {
        return new Multi(Condition.Operator.OR, Arrays.asList(conditions));
    }

    public static MultiCondition and(Condition... conditions) {
        return new Multi(Condition.Operator.AND, Arrays.asList(conditions));
    }

    private static class Single implements SingleCondition {

        private final String col;
        private final Operator operator;
        private final Object parameters;

        Single(String col, Operator operator, Object parameters) {
            this.col = col;
            this.operator = operator;
            this.parameters = parameters;
        }

        @Override
        public String column() {
            return this.col;
        }

        @Override
        public Object parameter() {
            return parameters;
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Single)) return false;
            Single single = (Single) o;
            return col.equals(single.col) &&
                    operator == single.operator &&
                    parameters.equals(single.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(col, operator, parameters);
        }
    }

    private static class Multi implements MultiCondition {

        private final Operator operator;
        private final List<Condition> conditions;

        Multi(Operator operator, List<Condition> conditions) {
            this.operator = operator;
            this.conditions = Collections.unmodifiableList(conditions);
        }

        @Override
        public List<Condition> conditions() {
            return conditions;
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Multi)) return false;
            Multi single = (Multi) o;
            return conditions.equals(single.conditions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(conditions);
        }
    }
}
