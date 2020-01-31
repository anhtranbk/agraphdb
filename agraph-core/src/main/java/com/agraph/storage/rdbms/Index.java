package com.agraph.storage.rdbms;

import java.util.List;

public interface Index {

    String table();

    String name();

    boolean isUnique();

    List<String> columns();
}
