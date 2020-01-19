package com.agraph.storage;

import java.util.List;

public interface Index {

    String table();

    String name();

    boolean isUnique();

    List<String> columns();
}
