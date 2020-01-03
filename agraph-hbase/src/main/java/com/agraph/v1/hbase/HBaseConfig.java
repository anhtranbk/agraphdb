package com.agraph.v1.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HBaseConfig {

    public static Configuration loadConfig() {
        Configuration conf = HBaseConfiguration.create();
        String runtimePath = System.getProperty("hbase.conf");
        if (runtimePath != null) {
            conf.addResource(new Path(runtimePath));
        }
        return conf;
    }
}
