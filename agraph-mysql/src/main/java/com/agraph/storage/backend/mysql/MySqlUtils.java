package com.agraph.storage.backend.mysql;

import com.agraph.common.tuple.Tuple2;
import com.agraph.storage.RowEntry;
import com.agraph.storage.rdbms.query.Condition;
import com.agraph.storage.rdbms.query.MultiCondition;
import com.agraph.storage.rdbms.query.SingleCondition;
import com.agraph.storage.rdbms.schema.Argument;
import com.agraph.storage.rdbms.schema.DBType;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MySqlUtils {

    public static String dbTypeToString(DBType type, int length) {
        switch (type) {
            case BLOB:
            case CLOB:
            case BINARY:
            case VARBINARY:
            case CHAR:
            case VARCHAR:
                return type.name().toLowerCase() + "(" + length + ")";
            case BOOLEAN:
                return "tinyint";
            case TIMESTAMP:
                return "datetime";
            default:
                return type.name().toLowerCase();
        }
    }

    public static List<Object> parseConditionArguments(Condition condition) {
        List<Object> args = new ArrayList<>();
        Stack<Condition> stack = new Stack<>();
        stack.push(condition);

        while (!stack.empty()) {
            Condition cc = stack.pop();
            if (cc.isMulti()) {
                MultiCondition mc = (MultiCondition) cc;
                for (int i = mc.conditions().size() - 1; i >= 0; i--) {
                    stack.push(mc.conditions().get(i));
                }
            } else {
                SingleCondition sc = (SingleCondition) cc;
                args.add(sc.arguments());
            }
        }
        return args;
    }

    public static String buildInsertTemplate(String table, RowEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(table).append("(");

        int i = 0;
        StringBuilder sb2 = new StringBuilder();
        for (Tuple2<String, Argument> tuple2 : entry.allKeysAndValues()) {
            if (i++ > 0) {
                sb.append(",");
                sb2.append(",");
            }
            sb.append(tuple2._1);
            sb2.append("%s");
        }
        sb.append(") VALUES (");
        sb.append(sb2.toString());
        sb.append(")");

        return sb.toString();
    }

    public static String buildUpdateTemplate(String table, RowEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(table);
        sb.append(" SET ");

        int i = 0;
        for (Tuple2<String, Argument> tuple2 : entry.values()) {
            if (i++ > 0) sb.append(",");
            sb.append(tuple2._1);
            sb.append("=%s");
        }

        // build where condition
        buildWhereCondition(sb, entry);

        return sb.toString();
    }

    public static String buildUpsertTemplate(String table, RowEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildInsertTemplate(table, entry));

        // on duplicate key then update
        sb.append("\nON DUPLICATE KEY UPDATE ");
        int i = 0;
        for (Tuple2<String, Argument> tuple2 : entry.values()) {
            if (i++ > 0) sb.append(",");
            sb.append(tuple2._1);
            sb.append("=%s");
        }
        return sb.toString();
    }

    public static String buildRemoveTemplate(String table, RowEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(table);
        buildWhereCondition(sb, entry);
        return sb.toString();
    }

    public static void buildWhereCondition(StringBuilder sb, RowEntry entry) {
        sb.append(" WHERE ");
        int i = 0;
        for (Tuple2<String, Argument> key : entry.keys()) {
            if (i++ > 0) sb.append(" AND ");
            sb.append(key._1);
            sb.append("=%s");
        }
    }

    public static List<Argument> buildInsertArgs(RowEntry entry) {
        List<Argument> args = new ArrayList<>(entry.columnsSize());
        for (Tuple2<String, Argument> tuple2 : entry.allKeysAndValues()) {
            args.add(tuple2._2);
        }
        return args;
    }

    public static List<Argument> buildUpdateArgs(RowEntry entry) {
        List<Argument> args = new ArrayList<>(entry.columnsSize());
        for (Tuple2<String, Argument> tuple2 : Iterables.concat(entry.values(), entry.keys())) {
            args.add(tuple2._2);
        }
        return args;
    }

    public static List<Argument> buildRemoveArgs(RowEntry entry) {
        List<Argument> args = new ArrayList<>(entry.keys().size());
        for (Tuple2<String, Argument> tuple2 : entry.keys()) {
            args.add(tuple2._2);
        }
        return args;
    }

    public static List<Argument> buildUpsertArgs(RowEntry entry) {
        Iterable<Tuple2<String, Argument>> fields = Iterables.concat(
                entry.keys(), entry.values(), entry.values());
        List<Argument> args = new ArrayList<>(entry.columnsSize() + entry.values().size());
        for (Tuple2<String, Argument> tuple2 : fields) {
            args.add(tuple2._2);
        }
        return args;
    }
}
