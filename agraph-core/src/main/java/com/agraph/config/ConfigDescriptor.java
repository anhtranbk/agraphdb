package com.agraph.config;

import com.agraph.common.util.DateTimes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDescriptor {

    String name();

    String defaultValue() default "";

    double minValue() default Long.MIN_VALUE;

    double maxValue() default Long.MAX_VALUE;

    boolean allowNull() default true;

    String datetimeFormat() default DateTimes.ISO_FORMAT;

    String collectionDelimiter() default ",";
}
