package com.agraph.config;

import com.agraph.common.util.DateTimes;
import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Configurable {

    default void configure(Config conf) {
        Class<?> cls = this.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigDescriptor.class)) continue;
            try {
                field.setAccessible(true);
                ConfigDescriptor descriptor = field.getAnnotation(ConfigDescriptor.class);
                String name = descriptor.name();
                String defVal = descriptor.defaultValue();

                if (!conf.containsKey(name) && !descriptor.allowNull()) {
                    throw new ConfigException("Missing config value for key: " + name);
                }

                Class<?> type = field.getType();
                if (type.isAssignableFrom(Integer.class) || type.equals(int.class)) {
                    int val = conf.getInt(name, Integer.parseInt(defVal));
                    validateNumberField(descriptor, val);
                    field.setInt(this, val);

                } else if (type.isAssignableFrom(Long.class) || type.equals(long.class)) {
                    long val = conf.getLong(name, Long.parseLong(defVal));
                    validateNumberField(descriptor, val);
                    field.setLong(this, val);

                } else if (type.isAssignableFrom(Boolean.class) || type.equals(boolean.class)) {
                    field.setBoolean(this, conf.getBool(name, Boolean.parseBoolean(defVal)));

                } else if (type.isAssignableFrom(Double.class) || type.equals(double.class)) {
                    double val = conf.getDouble(name, Double.parseDouble(defVal));
                    validateNumberField(descriptor, val);
                    field.setDouble(this, val);

                } else if (type.isAssignableFrom(Float.class) || type.equals(float.class)) {
                    float val = conf.getFloat(name, Float.parseFloat(defVal));
                    validateNumberField(descriptor, val);
                    field.setFloat(this, val);

                } else if (type.isAssignableFrom(Date.class)) {
                    Date defDate = DateTimes.parse(defVal, descriptor.datetimeFormat());
                    field.set(this, conf.getDateTime(name, descriptor.datetimeFormat(), defDate));

                } else if (type.isAssignableFrom(Collection.class)) {
                    field.set(this, conf.getCollection(name, descriptor.collectionDelimiter()));

                } else {
                    field.set(this, conf.getString(name, defVal));
                }
            } catch (Exception e) {
                throw new ConfigException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }

    static void validateNumberField(ConfigDescriptor descriptor, double val) {
        double max = descriptor.maxValue();
        double min = descriptor.minValue();
        Preconditions.checkArgument(val <= max && val >= min,
                "Field value %s is not in range (%s, %s)", val, min, max);
    }
}
