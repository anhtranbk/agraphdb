package com.agraph.storage.rdbms.query;

import java.util.List;

public interface MultiCondition extends Condition {

    List<Condition> conditions();
}
