package com.agraph;

public enum State {

    /**
     * The entity has been newly created and not yet persisted.
     */
    NEW,
    /**
     * The entity has been loaded from the database without properties.
     */
    EMPTY,
    /**
     * The entity has been loaded from the database and has not changed
     * after initial loading.
     */
    LOADED,
    /**
     * The entity has changed after being loaded from the database by adding and/or deleting relations.
     */
    MODIFIED,
    /**
     * The entity has been deleted but not yet erased from the database.
     */
    REMOVED
}
