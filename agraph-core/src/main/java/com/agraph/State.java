package com.agraph;

public enum State {

    /**
     * The entity has been newly created and not yet persisted.
     */
    NEW{
        @Override
        public State nextState() {
            return State.LAGGED;
        }
    },
    /**
     * The entity's ID has been loaded from the database with empty properties
     * or has properties that has lagged with the database and need to refresh
     * before return to the end-user.
     */
    LAGGED{
        @Override
        public State nextState() {
            return State.LOADED;
        }
    },
    /**
     * The entity has been loaded from the database and has not changed
     * after initial loading.
     */
    LOADED{
        @Override
        public State nextState() {
            return State.MODIFIED;
        }
    },
    /**
     * The entity has changed after being loaded from the database by adding
     * and/or deleting relations.
     */
    MODIFIED{
        @Override
        public State nextState() {
            return State.LAGGED;
        }
    },
    /**
     * The entity has been deleted but not yet erased from the database.
     */
    REMOVED{
        @Override
        public State nextState() {
            return null;
        }
    };

    public abstract State nextState();
}
