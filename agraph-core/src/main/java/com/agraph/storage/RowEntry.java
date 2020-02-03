package com.agraph.storage;

import com.agraph.common.tuple.Tuple2;
import com.agraph.storage.rdbms.schema.Argument;
import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
public class RowEntry {

    private final List<Tuple2<String, Argument>> keys;
    private final List<Tuple2<String, Argument>> values;

    public RowEntry(List<Tuple2<String, Argument>> keys,
                    List<Tuple2<String, Argument>> values) {
        this.keys = keys;
        this.values = values;
    }

    public Iterable<Tuple2<String, Argument>> allKeysAndValues() {
        return Iterables.concat(keys, values);
    }

    /**
     * @return Number of both key and value fields
     */
    public int columnsSize() {
        return this.keys.size() + this.values.size();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Tuple2<String, Argument>> keys = new ArrayList<>();
        private final List<Tuple2<String, Argument>> values = new ArrayList<>();

        public Builder addKey(String name, Argument arg) {
            this.keys.add(new Tuple2<>(name, arg));
            return this;
        }

        public Builder addValue(String name, Argument arg) {
            this.values.add(new Tuple2<>(name, arg));
            return this;
        }

        public RowEntry build() {
            return new RowEntry(keys, values);
        }
    }
}
