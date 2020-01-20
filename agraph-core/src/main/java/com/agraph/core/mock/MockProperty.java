package com.agraph.core.mock;

import com.agraph.core.AGraphProperty;
import com.agraph.core.AbstractElement;
import org.apache.tinkerpop.gremlin.structure.Element;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MockProperty<V> extends AGraphProperty<V> {

    public MockProperty(AbstractElement owner, String key, V value) {
        super(owner, key, value);
    }

    @Override
    public Element element() {
        return this.owner;
    }
}
