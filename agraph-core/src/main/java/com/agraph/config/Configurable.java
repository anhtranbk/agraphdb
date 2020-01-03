package com.agraph.config;

import com.agraph.common.utils.DateTimes;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Configurable {

    default void configure(Config config) {
        Class<?> cls = this.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigDescriptor.class)) continue;
            try {
                field.setAccessible(true);
                ConfigDescriptor descriptor = field.getAnnotation(ConfigDescriptor.class);
                String name = descriptor.value();
                String defVal = descriptor.defaultValue();
                Class<?> type = field.getType();

                if (type.isAssignableFrom(Integer.class) || type.equals(int.class)) {
                    field.setInt(this, config.getInt(name, Integer.parseInt(defVal)));
                } else if (type.isAssignableFrom(Long.class) || type.equals(long.class)) {
                    field.setLong(this, config.getLong(name, Long.parseLong(defVal)));
                } else if (type.isAssignableFrom(Boolean.class) || type.equals(boolean.class)) {
                    field.setBoolean(this, config.getBool(name, Boolean.parseBoolean(defVal)));
                } else if (type.isAssignableFrom(Double.class) || type.equals(double.class)) {
                    field.setDouble(this, config.getDouble(name, Double.parseDouble(defVal)));
                } else if (type.isAssignableFrom(Float.class) || type.equals(float.class)) {
                    field.setFloat(this, (float) config.getDouble(name, Float.parseFloat(defVal)));
                } else if (type.isAssignableFrom(Date.class)) {
                    Date defDate = DateTimes.parse(defVal, descriptor.datetimeFormat());
                    field.set(this, config.getDateTime(name, descriptor.datetimeFormat(), defDate));
                } else if (type.isAssignableFrom(Collection.class)) {
                    field.set(this, config.getCollection(name, descriptor.collectionDelimiter()));
                } else {
                    field.set(this, config.getString(name, defVal));
                }
            } catch (Exception e) {
                throw new ConfigException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }
}
