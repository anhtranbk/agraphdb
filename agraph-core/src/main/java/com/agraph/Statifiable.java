package com.agraph;

public interface Statifiable {

    State state();

    void updateState(State state);

    default boolean isNew() {
        return state().equals(State.NEW);
    }

    default boolean isEmpty() {
        return state().equals(State.EMPTY);
    }

    default boolean isLoaded() {
        return state().equals(State.LOADED);
    }

    default boolean isModified() {
        return state().equals(State.MODIFIED);
    }

    default boolean isRemoved() {
        return state().equals(State.REMOVED);
    }

    default boolean isPresent() {
        return !state().equals(State.REMOVED);
    }
}
