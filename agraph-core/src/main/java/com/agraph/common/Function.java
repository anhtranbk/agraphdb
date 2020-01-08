package com.agraph.common;

import org.jetbrains.annotations.NotNull;

public interface Function<S, R> {

    R apply(@NotNull S var);
}
