package com.agraph.storage;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collection;

@Accessors(fluent = true)
@Getter
public class Mutation {

    private final String table;
    private final Action action;
    private final Collection<RowEntry> entries;

    public Mutation(String table, Action action, Collection<RowEntry> entries) {
        this.table = table;
        this.action = action;
        this.entries = entries;
    }

    public Mutation(String table, Action action, RowEntry... entries) {
        this(table, action, Arrays.asList(entries));
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int entrySize() {
        return this.entries.size();
    }

    public enum Action {
        ADD, UPDATE, UPSERT, APPEND, REMOVE
    }
}
