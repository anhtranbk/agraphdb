package com.agraph.common.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamSupports {

    public static <T> Stream<T> stream(Iterable<T> source) {
        return stream(source, false);
    }

    public static <T> Stream<T> stream(Iterable<T> source, boolean parallel) {
        return StreamSupport.stream(source.spliterator(), parallel);
    }
}
