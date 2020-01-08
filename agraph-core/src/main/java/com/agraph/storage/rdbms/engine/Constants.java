package com.agraph.storage.rdbms.engine;

public interface Constants {

    String VERTEX_TABLE = "vertices";
    String VERTEX_LARGE_PROPS_TABLE = "vertex_props";
    String EDGE_TABLE = "edges";
    String EDGE_PROPS_TABLE = "edge_props";
    String SYSTEM_TABLE = "system";

    String ID_COL = "id";
    String REF_ID_COL = "rid";
    String LABEL_COL = "lb";
    String KEY_COL = "k";
    String VALUE_COL = "v";
    String VERTEX_SRC_COL = "i";
    String VERTEX_DST_COL = "o";
    String VERTEX_SRC_LABEL_COL = "il";
    String VERTEX_DST_LABEL_COL = "ol";
}
