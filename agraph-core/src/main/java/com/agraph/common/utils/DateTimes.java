package com.agraph.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DateTimes {

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Date parseFromIsoFormat(String source) {
        return parse(source, ISO_FORMAT);
    }

    public static String toIsoFormat(Date date) {
        DateFormat df = new SimpleDateFormat(ISO_FORMAT);
        return df.format(date);
    }

    public static Date parse(String source, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(source);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date add(int field, int amount) {
        return add(field, amount, new Date());
    }

    public static Date add(int field, int amount, Date init) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(init);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * @return current date as string in format yyyy-MM-dd
     */
    public static String currentDateAsString() {
        return DateTimes.format(new Date(), "yyyy-MM-dd");
    }

    /**
     * @return current date as bytes in format yyyy-MM-dd
     */
    public static byte[] currentDateAsBytes() {
        return DateTimes.format(new Date(), "yyyy-MM-dd").getBytes();
    }
}
