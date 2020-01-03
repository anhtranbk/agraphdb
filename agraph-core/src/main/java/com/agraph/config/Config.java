package com.agraph.config;

import com.agraph.common.utils.DateTimes;
import com.agraph.common.utils.Strings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class Config extends Properties {

    private static final String LIST_DELIMITER = ",";
    private static final String CONF_ENV = "agraph.conf";
    private static final String DEFAULT_NAME = "agraph.properties";

    /**
     * @param path Path to file use as default resource to load properties
     *             when init new Config object with empty constructor
     */
    public static void setDefaultResourcePath(String path) {
        defaultResPath = path;
    }

    private static final Properties defaultProps = new Properties();
    private static String defaultResPath = System.getProperty(CONF_ENV);

    static {
        try {
            // default properties from resource file
            defaultProps.load(Config.class.getClassLoader().getResourceAsStream(DEFAULT_NAME));
        } catch (IOException e) {
            Throwable t = new IOException(Strings.format("Cannot find %s in classpath", DEFAULT_NAME));
            t.printStackTrace(new PrintStream(System.out));
        }
    }

    public Config() {
        this(defaultResPath);
    }

    public Config(String path) {
        try {
            // extra properties from file file will have higher priority
            addResource(path);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public Config(InputStream is) {
        try {
            addResource(is);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public void addResource(InputStream is) throws IOException {
        this.addResource(is, false);
    }

    public void addResource(InputStream is, boolean closeAfterLoad) throws IOException {
        try {
            this.load(is);
        } finally {
            if (closeAfterLoad) is.close();
        }
    }

    public void addResource(String path) throws IOException {
        if (path != null) {
            this.addResource(new FileInputStream(path), true);
        }
    }

    public <T> void set(String key, T val) {
        setProperty(key, val.toString());
    }

    public void setDateTime(String key, Date date) {
        setDateTime(key, date, DateTimes.ISO_FORMAT);
    }

    public void setDateTime(String key, Date date, String format) {
        setProperty(key, DateTimes.format(date, format));
    }

    public void setCollection(String key, String... elements) {
        setCollection(key, Arrays.asList(elements));
    }

    public void setCollection(String key, Iterable<String> list) {
        setProperty(key, Strings.join(list, LIST_DELIMITER));
    }

    public void setClass(String key, Class<?> cls) {
        setProperty(key, cls.getName());
    }

    public void setClasses(String key, Class<?>... classes) {
        setClasses(key, Arrays.asList(classes));
    }

    public void setClasses(String key, Collection<Class<?>> classes) {
        List<String> clsNames = new ArrayList<>(classes.size());
        for (Class<?> cls : classes) {
            clsNames.add(cls.getName());
        }
        setCollection(key, clsNames);
    }

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        return (value != null) ? value : defaultProps.getProperty(key);
    }

    public <T> T get(String key, T defVal) {
        try {
            return (T) get(key);
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public int getInt(String key, int defVal) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public long getLong(String key, long defVal) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public double getDouble(String key, double defVal) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public double getFloat(String key, float defVal) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public boolean getBool(String key, boolean defVal) {
        try {
            return Boolean.parseBoolean(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public Collection<String> getCollection(String key) {
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
        return getDateTime(key, DateTimes.ISO_FORMAT, defVal);
    }

    public Date getDateTime(String key, String format, Date defVal) {
        try {
            return DateTimes.parse(getProperty(key), format);
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
        Collection<String> clsNames = getCollection(key);
        List<Class<?>> classes = new ArrayList<>(clsNames.size());
        for (String className : getCollection(key)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (Object key : keySet()) {
            map.put(key.toString(), this.get(key));
        }
        return map;
    }

    @Override
    public synchronized String toString() {
        Set<Object> keySet = new TreeSet<>();
        keySet.addAll(this.keySet());
        keySet.addAll(defaultProps.keySet());

        List<Object> list = new ArrayList<>(keySet);
        list.sort(Comparator.comparing(Object::toString));

        StringBuilder sb = new StringBuilder("Configuration properties:\n");
        list.forEach(key -> sb.append(String.format(Locale.US, "\t%s = %s\n",
                key.toString(), getProperty(key.toString(), ""))));
        return sb.toString();
    }
}
