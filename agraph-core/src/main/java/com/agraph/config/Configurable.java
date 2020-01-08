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
                    int val = config.getInt(name, Integer.parseInt(defVal));
                    validateNumberField(descriptor, val);
                    field.setInt(this, val);

                } else if (type.isAssignableFrom(Long.class) || type.equals(long.class)) {
                    long val = config.getLong(name, Long.parseLong(defVal));
                    validateNumberField(descriptor, val);
                    field.setLong(this, val);

                } else if (type.isAssignableFrom(Boolean.class) || type.equals(boolean.class)) {
                    field.setBoolean(this, config.getBool(name, Boolean.parseBoolean(defVal)));

                } else if (type.isAssignableFrom(Double.class) || type.equals(double.class)) {
                    double val = config.getDouble(name, Double.parseDouble(defVal));
                    validateNumberField(descriptor, val);
                    field.setDouble(this, val);

                } else if (type.isAssignableFrom(Float.class) || type.equals(float.class)) {
                    float val = config.getFloat(name, Float.parseFloat(defVal));
                    validateNumberField(descriptor, val);
                    field.setFloat(this, val);

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

    static void validateNumberField(ConfigDescriptor descriptor, double val) {
        double max = descriptor.maxValue();
        double min = descriptor.minValue();
        Preconditions.checkArgument(val <= max && val >= min, "Field value is not in range");
    }
}
