package com.agraph.storage.rdbms.schema;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
@Getter
public class TableDefine {

    private final String name;
    private final List<Column> nonKeyColumns;
    private final List<Column> keyColumns;

    public TableDefine(String name, List<Column> keyColumns, List<Column> nonKeyColumns) {
        this.name = name;
        this.keyColumns = keyColumns;
        this.nonKeyColumns = nonKeyColumns;
    }

    public Iterable<Column> allColumns() {
        return Iterables.concat(this.keyColumns, this.nonKeyColumns);
    }

    public TableDefine addPartitionColumns(Column... columns) {
        return this;
    }

    public TableDefine addClusteringColumns(Column... columns) {
        return this;
    }

    public TableDefine addNormalColumns(Column... columns) {
        return this;
    }

    public static TableDefine create(String name, List<Column> keyColumns, List<Column> nonKeyColumns) {
        return new TableDefine(name, keyColumns, nonKeyColumns);
    }
}
