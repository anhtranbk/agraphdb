package com.agraph.storage.rdbms.query;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Accessors(fluent = true)
@Getter
public class Query {

    private final String table;
    private final Condition condition;
    private final List<String> columns;
    private final List<Order> orders;
    private final int offset;
    private final int limit;
    private final boolean showDeleted;
    private final boolean showHidden;

    private Query(String table, Condition condition, List<String> columns, List<Order> orders,
                  int offset, int limit, boolean showDeleted, boolean showHidden) {
        this.table = table;
        this.condition = condition;
        this.columns = columns;
        this.orders = orders;
        this.offset = offset;
        this.limit = limit;
        this.showDeleted = showDeleted;
        this.showHidden = showHidden;
    }

    public static Builder builder(String table) {
        return new Builder(table);
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    public static class Builder {
        private String table;
        private Condition condition;
        private List<String> columns = new ArrayList<>(8);
        private List<Order> orders = new ArrayList<>(8);
        private int offset = 0;
        private int limit = Integer.MAX_VALUE;
        private boolean showDeleted = false;
        private boolean showHidden = false;

        Builder(String table) {
            this.table = table;
        }

        public Builder addColumns(String... cols) {
            columns.addAll(Arrays.asList(cols));
            return this;
        }

        public Builder addOrders(Order... orders) {
            this.orders.addAll(Arrays.asList(orders));
            return this;
        }

        public Query build() {
            Preconditions.checkArgument(offset < 0, "Invalid offset %d", offset);
            Preconditions.checkArgument(limit < 0, "Invalid limit %d", limit);

            return new Query(table, condition, columns, orders, offset, limit, showDeleted, showHidden);
        }
    }
}
