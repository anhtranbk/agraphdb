package com.agraph.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Systems {

    @SuppressWarnings("unchecked")
    public static <T> int compare(Comparable<T> c1, Comparable<T> c2) {
        return c1.compareTo((T) c2);
    }

    public static <E> boolean notEquals(E e1, E e2) {
        return !e1.equals(e2);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        } else if (o.getClass().isArray()) {
            return ((Object[]) ((Object[]) o)).length == 0;
        } else if (o instanceof Collection) {
            return ((Collection) o).isEmpty();
        } else {
            return o instanceof Map && ((Map) o).isEmpty();
        }
    }

    public static void systemExit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    public static void systemError(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void systemError(Throwable throwable) {
        throwable.printStackTrace();
        System.exit(1);
    }

    public static String currentTimePrefix() {
        return DateTimes.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " - ";
    }

    public static long inverseTimestamp() {
        return Long.MAX_VALUE - System.currentTimeMillis();
    }

    public static void addShutdownHook(Runnable target) {
        Runtime.getRuntime().addShutdownHook(new Thread(target));
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public static int[] newIntArray(int from, int to) {
        int[] arr = new int[to - from];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = to + i;
        }
        return arr;
    }
}
