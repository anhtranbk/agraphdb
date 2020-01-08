package com.agraph.v1.cassandra;

import com.datastax.driver.core.Row;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CassandraUtils {

    public static Map<String, Object> convertRowToMap(Row row) {
        Map<String, Object> map = new HashMap<>(row.getMap("ex", String.class, String.class));
//        map.put("id", row.getLong("uid"));
        map.put("address", row.getString("addr"));
        map.put("birthday", row.getString("bday"));
        map.put("job", row.getString("job"));
        map.put("name", row.getString("name"));
        map.put("location", row.getString("loc"));
        map.put("phones", row.getSet("phones", String.class));
        map.put("emails", row.getSet("emails", String.class));

        String gender = row.getString("gender");
        if (gender != null)
            map.put("gender", (gender.equals("f") ? "female" : "male"));

        List<Long> uids = new ArrayList<>(row.getSet("uids", Long.class));
        map.put("uids", uids);

        Map<String, String> mapFb = row.getMap("fb", String.class, String.class);
        List<Map<String, String>> listFb = new ArrayList<>();
        mapFb.forEach((key, value) -> listFb.add(ImmutableMap.of("id", key, "name", value)));
        map.put("facebooks", listFb);

        Map<String, String> mapFr = row.getMap("fr", String.class, String.class);
        List<Map<String, String>> listFr = new ArrayList<>();
        mapFr.forEach((key, value) -> listFr.add(ImmutableMap.of("domain", key, "id", value)));
        map.put("forums", listFr);

        return map;
    }
}
