package com.agraph.v1.hbase;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
interface Constants {

    String TB_VERTEX = "vertices";
    String TB_EDGE = "edges";

    byte[] HIDDEN_PREFIX = "_".getBytes();
    byte[] SYSTEM_PREFIX = "__".getBytes();

    byte[] CQ_CREATED_DATE = "__dt".getBytes();
    byte[] CQ_LAST_MODIFIED = "__ts".getBytes();
    byte[] CQ_DST_LABEL = "dstlb".getBytes();

    byte[] DIRECTION_IN = "i".getBytes();
    byte[] DIRECTION_OUT = "o".getBytes();
    byte[] PROPERTIES = "p".getBytes();
}
