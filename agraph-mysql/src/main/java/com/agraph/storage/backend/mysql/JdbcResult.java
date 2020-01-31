package com.agraph.storage.backend.mysql;

import com.agraph.storage.Result;
import com.agraph.storage.backend.BackendException;

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JdbcResult implements Result {

    private final ResultSet rs;

    public JdbcResult(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public boolean getBoolean(String col) {
        try {
            return rs.getBoolean(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public byte getByte(String col) {
        try {
            return rs.getByte(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public short getSmallInt(String col) {
        try {
            return rs.getShort(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public int getInt(String col) {
        try {
            return rs.getInt(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public long getBigInt(String col) {
        try {
            return rs.getLong(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public float getFloat(String col) {
        try {
            return rs.getFloat(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public double getDouble(String col) {
        try {
            return rs.getDouble(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public byte[] getBytes(String col) {
        try {
            return rs.getBytes(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public String getString(String col) {
        try {
            return rs.getString(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public Date getDate(String col) {
        try {
            return rs.getDate(col);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public long getTimestamp(String col) {
        try {
            return rs.getTimestamp(col).getTime();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public InputStream getBlob(String col) {
        try {
            return rs.getBlob(col).getBinaryStream();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public Reader getClob(String col) {
        try {
            return rs.getClob(col).getCharacterStream();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }
}
