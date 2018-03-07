package com.vcc.bigdata.common.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class Properties extends java.util.Properties {

    public Properties toSubProperties(String group, String name) {
        return new SubProperties(group, name, this);
    }

    public Properties toSubProperties(String name) {
        return new SubProperties(name, this);
    }

    public Properties toSubProperties(Class<?> clazz) {
        return new SubProperties(clazz, this);
    }

    public <T> T getProperty(String key, T defVal) {
        try {
            return (T) get(key);
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public int getIntProperty(String key, int defVal) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public long getLongProperty(String key, long defVal) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public double getDoubleProperty(String key, double defVal) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public boolean getBoolProperty(String key, boolean defVal) {
        try {
            return Boolean.parseBoolean(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public List<String> getCollection(String key) {
        try {
            return Arrays.asList(getProperty(key).split(","));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    public List<String> getCollection(String key, String delimiter) {
        try {
            return Arrays.asList(getProperty(key).split(delimiter));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    public Date getDateTime(String key, Date defVal) {
        return getDateTime(key, "yyyy-MM-dd HH:mm:ss", defVal);
    }

    public Date getDateTime(String key, String format, Date defVal) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(getProperty(key));
        } catch (Exception e) {
            return defVal;
        }
    }

    public Class<?> getClass(String key, Class<?> defVal) {
        try {
            return Class.forName(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public Collection<Class<?>> getClasses(String key) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        for (String className : getCollection(key)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (Object key: keySet()) {
            map.put(key.toString(), this.get(key));
        }
        return map;
    }
}
