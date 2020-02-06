package com.agraph.config;

import org.apache.commons.configuration.Configuration;

import java.util.Iterator;

public class ConfigUtils {

    public static Config fromApacheConfiguration(Configuration apacheConf) {
        Config conf = new Config();
        Iterator<String> it = apacheConf.getKeys();
        while (it.hasNext()) {
            String key = it.next();
            Object val = apacheConf.getProperty(key);
            conf.set(key, val);
        }
        return conf;
    }

    public static Configuration toApacheConfiguration(Config conf) {
        throw new UnsupportedOperationException();
    }
}
