package com.agraph.storage.rdbms;

import java.util.List;

public interface Index {

    String name();

    boolean isUnique();

    List<String> columns();
}
