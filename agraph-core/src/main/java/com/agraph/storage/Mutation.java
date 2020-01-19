package com.agraph.storage;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Accessors(fluent = true)
@Getter
public class Mutation {

    private final String table;
    private final Action action;
    private final Iterable<RowEntry> entries;

    public Mutation(String table, Action action, Iterable<RowEntry> entries) {
        this.table = table;
        this.action = action;
        this.entries = entries;
    }

    public Mutation(String table, Action action, RowEntry... entries) {
        this(table, action, Arrays.asList(entries));
    }

    public enum Action {
        ADD, UPDATE, ADD_OR_UPDATE, APPEND, REMOVE
    }
}
