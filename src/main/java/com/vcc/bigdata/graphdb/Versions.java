package com.vcc.bigdata.graphdb;

import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Versions {

    public static final String VERSION_PREFIX = "_v";
    public static final String DELIMITER = "@";
    public static final int MIN_VERSION = 1;

    public static void setVersion(String type, int version, Element element) {
        String key = VERSION_PREFIX + DELIMITER + type;
        element.putProperty(key, String.valueOf(version));
    }

    public static int getVersion(String type, Element element) {
        String key = VERSION_PREFIX + DELIMITER + type;
        return Integer.parseInt(element.property(key).toString());
    }

    public static Map<String, Integer> getAllVersions(Element element) {
        Map<String, Integer> map = new TreeMap<>();
        String prefix = VERSION_PREFIX + DELIMITER;

        for (Map.Entry<String, ?> e : element.properties().entrySet()) {
            try {
                String key = e.getKey();
                if (!key.startsWith(prefix)) continue;
                String type = key.split(DELIMITER)[1];
                map.put(type, Integer.valueOf(e.getValue().toString()));
            } catch (IndexOutOfBoundsException | NullPointerException | NumberFormatException ignored) {
            }
        }
        return map;
    }

    public static boolean checkElementOutOfDate(Element element, Map<String, Integer> latestVersionMapping) {
        Map<String, Integer> versions = Versions.getAllVersions(element);
        for (Map.Entry<String, Integer> e : versions.entrySet()) {
            String type = e.getKey();
            // true if at least one type is latest version
            if (e.getValue() >= latestVersionMapping.getOrDefault(type, Versions.MIN_VERSION)) {
                return false;
            }
        }
        return true;
    }
}
