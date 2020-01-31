package com.agraph.storage.backend.mysql;

import com.agraph.storage.rdbms.schema.Argument;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Getter
@Accessors(fluent = true)
public class BatchStatement implements AutoCloseable {

    private final PreparedStatement preparedStatement;
    private int size;

    public BatchStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void setArgument(int i, Argument arg) throws SQLException {
        switch (arg.type()) {
            case BOOLEAN:
                this.preparedStatement.setBoolean(i, (Boolean) arg.value());
                break;
            case INT:
                this.preparedStatement.setInt(i, (Integer) arg.value());
                break;
            case SMALLINT:
                this.preparedStatement.setShort(i, (Short) arg.value());
                break;
            case BIGINT:
                this.preparedStatement.setLong(i, (Long) arg.value());
                break;
            case FLOAT:
                this.preparedStatement.setFloat(i, (Float) arg.value());
                break;
            case DOUBLE:
                this.preparedStatement.setDouble(i, (Double) arg.value());
                break;
            case VARCHAR:
            case CHAR:
                this.preparedStatement.setString(i, (String) arg.value());
                break;
            case VARBINARY:
            case BINARY:
                this.preparedStatement.setBytes(i, (byte[]) arg.value());
                break;
            case DATE:
                this.preparedStatement.setDate(i, javaDate2sqlDate((java.util.Date) arg.value()));
                break;
            case TIMESTAMP:
                this.preparedStatement.setTimestamp(i, new Timestamp((Long) arg.value()));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void addBatch() throws SQLException {
        this.preparedStatement.addBatch();
        this.size++;
    }

    public void executeBatch() throws SQLException {
        this.preparedStatement.executeBatch();
    }

    public void executeLargeBatch() throws SQLException {
        this.preparedStatement.executeLargeBatch();
    }

    @Override
    public void close() throws SQLException {
        this.preparedStatement.close();
    }

    static Date javaDate2sqlDate(java.util.Date date) {
        return new Date(date.getTime());
    }
}
