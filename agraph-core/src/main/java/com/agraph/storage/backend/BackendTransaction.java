package com.agraph.storage.backend;

import com.agraph.storage.Mutation;

import java.util.Collection;

public interface BackendTransaction {

    default void beforeCommit(Collection<Mutation> mutations) {
    }

    void commit();

    default void afterCommit() {
    }

    default void beforeRollback(Collection<Mutation> mutations) {
    }

    void rollback();

    default void afterRollback() {
    }

    default boolean autoCommit() {
        return false;
    }

    default void autoCommit(boolean autoCommit) {
        throw new UnsupportedOperationException();
    }
}
