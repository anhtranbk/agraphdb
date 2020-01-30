package com.agraph.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

public class Files2 {

    public static void writeProps(String path, Properties properties) throws IOException {
        String dirPath = path.substring(0, path.lastIndexOf(File.separator));
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Make intermediate folders failed");

        try (PrintWriter writer = new PrintWriter(new FileWriter(path, false))) {
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String next = enumeration.nextElement().toString();
                writer.println(next + " = " + properties.getProperty(next));
            }
            writer.println();
        }
    }
}
