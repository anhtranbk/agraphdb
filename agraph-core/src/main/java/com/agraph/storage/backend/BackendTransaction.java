package com.agraph.storage.backend;

import com.agraph.storage.Mutation;

import java.util.Collection;

public interface BackendTransaction {

    void beforeCommit(Collection<Mutation> mutations);

    void commit();

    void afterCommit();

    void beforeRollback(Collection<Mutation> mutations);

    void rollback();

    void afterRollback();

    boolean autoCommit();

    void autoCommit(boolean autoCommit);
}
