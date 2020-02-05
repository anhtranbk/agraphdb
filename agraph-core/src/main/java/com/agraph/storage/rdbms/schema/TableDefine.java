package com.agraph.storage.rdbms.schema;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class TableDefine {

    @Getter
    private final String name;
    private final List<Column> columns = new ArrayList<>();
    private final List<String> keys = new ArrayList<>();

    public TableDefine(String name) {
        this.name = name;
    }

    public TableDefine keys(String... keys) {
        this.keys.addAll(Arrays.asList(keys));
        return this;
    }

    public List<String> keys() {
        return Collections.unmodifiableList(this.keys);
    }

    public TableDefine columns(Column... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public List<Column> columns() {
        return Collections.unmodifiableList(columns);
    }

    public List<String> columnNames() {
        return this.columns.stream().map(Column::name).collect(Collectors.toList());
    }

    public static TableDefine create(String name) {
        return new TableDefine(name);
    }
}
