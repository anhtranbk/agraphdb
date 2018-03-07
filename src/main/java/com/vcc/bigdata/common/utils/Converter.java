package com.vcc.bigdata.common.utils;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Converter<S, R> {

    R convert(S source);
}
