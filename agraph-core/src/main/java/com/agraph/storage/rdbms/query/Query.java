package com.agraph.storage.rdbms.query;

import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Query {

    String table();

    List<String> columns();

    Condition condition();

    List<Order> orders();

    int offset();

    int limit();

    static Builder builder(String table) {
        return new Builder(table);
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    class Builder {
        String table;
        Condition condition;
        List<String> columns = new ArrayList<>(8);
        List<Order> orders = new ArrayList<>(8);
        int offset = 0;
        int limit = -1;

        Builder(String table) {
            this.table = table;
        }

        public Builder addColumn(String... cols) {
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

            final Builder delegate = Builder.this;
            return new Query() {
                @Override
                public String table() {
                    return delegate.table;
                }

                @Override
                public List<String> columns() {
                    return Collections.unmodifiableList(delegate.columns);
                }

                @Override
                public Condition condition() {
                    return delegate.condition;
                }

                @Override
                public List<Order> orders() {
                    return Collections.unmodifiableList(delegate.orders);
                }

                @Override
                public int offset() {
                    return delegate.offset;
                }

                @Override
                public int limit() {
                    return delegate.limit;
                }
            };
        }
    }
}
