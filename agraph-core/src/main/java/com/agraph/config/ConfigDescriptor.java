package com.agraph.config;

import com.agraph.common.util.DateTimes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDescriptor {

    String value();

    String defaultValue() default "0";

    double minValue() default 0;

    double maxValue() default 0;

    String datetimeFormat() default DateTimes.ISO_FORMAT;

    String collectionDelimiter() default ",";
}
