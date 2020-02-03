package com.agraph;

public interface Statifiable {

    State state();

    void updateState(State state);

    default boolean isNew() {
        return state() == State.NEW;
    }

    default boolean isLagged() {
        return state() == State.LAGGED;
    }

    default boolean isLoaded() {
        return state() == State.LOADED;
    }

    default boolean isModified() {
        return state() == State.MODIFIED;
    }

    default boolean isRemoved() {
        return state() == State.REMOVED;
    }

    default boolean isPresent() {
        return state() != State.REMOVED;
    }
}
