package com.agraph.storage.rdbms.query;

public interface SingleCondition extends Condition {

    String column();

    Object parameter();
}
