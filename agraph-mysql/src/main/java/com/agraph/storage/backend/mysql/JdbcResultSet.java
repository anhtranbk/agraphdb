package com.agraph.storage.backend.mysql;

import com.agraph.storage.Result;
import com.agraph.storage.backend.BackendException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class JdbcResultSet implements Iterator<Result> {

    private final ResultSet rs;

    public JdbcResultSet(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public boolean hasNext() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public Result next() {
        return new JdbcResult(this.rs);
    }
}
