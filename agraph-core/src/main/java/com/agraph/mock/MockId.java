package com.agraph.mock;

import com.agraph.core.type.DataType;
import com.agraph.core.type.ElementId;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MockId extends ElementId {

    @Override
    public String asString() {
        return null;
    }

    @Override
    public DataType type() {
        return null;
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public boolean isVertex() {
        return false;
    }
}
