package com.agraphdb.common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Configuration extends Properties {

    private static final String CONF_ENV = "app.conf";
    private static final String DEFAULT_NAME = "config.properties";

    /**
     * Load properties and update into JVM system properties
     *
     * @param props object contains properties to load
     * @param propertyKeys property keys that will be updated
     */
    public static void setSystemPropertiesFromConfig(Properties props, String... propertyKeys) {
        for (String key : propertyKeys) {
            if (System.getProperty(key) != null) continue;
            System.setProperty(key, props.getProperty(key));
        }
    }

    /**
     * @param path Path to file use as default resource to load properties
     *             when init new Configuration object with empty constructor
     */
    public static void setDefaultResourcePath(String path) {
        defaultResPath = path;
    }

    private static final Properties defaultProps = new Properties();
    private static String defaultResPath = System.getProperty(CONF_ENV);

    static {
        try {
            // default properties from resource file
            defaultProps.load(Configuration.class.getClassLoader().getResourceAsStream(DEFAULT_NAME));
        } catch (IOException e) {
            Throwable t = new IOException("Cannot find config.properties in classpath");
            t.printStackTrace(new PrintStream(System.out));
        }
    }

    public Configuration() {
        this(defaultResPath);
    }

    public Configuration(String path) {
        try {
            // extra properties from file file will have higher priority
            addResource(path);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    public Configuration(InputStream is) {
        try {
            addResource(is);
        } catch (IOException e) {
            throw new ConfigurationException(e);
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

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        return (value != null) ? value : defaultProps.getProperty(key);
    }

    @Override
    public synchronized String toString() {
        Set<Object> keySet = new LinkedHashSet<>();
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
