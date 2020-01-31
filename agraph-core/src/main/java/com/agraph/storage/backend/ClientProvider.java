package com.agraph.storage.backend;

public interface ClientProvider<C> {

    C getClient();

    void shutdown();
}
