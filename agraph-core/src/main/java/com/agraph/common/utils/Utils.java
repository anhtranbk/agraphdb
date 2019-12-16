package com.agraph.common.utils;

import com.agraph.common.config.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> int compare(Comparable<T> c1, Comparable<T> c2) {
        return c1.compareTo((T) c2);
    }

    public static <T> T lastItem(Iterable<T> collection) {
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        Iterator<T> iterator = collection.iterator();
        T last = iterator.next();
        while (iterator.hasNext()) last = iterator.next();
        return last;
    }

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
            return o instanceof Map ? ((Map) o).isEmpty() : false;
        }
    }

    @Deprecated
    public static void sleepIgnoredException(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static ExecutorService newCachedThreadPool(int coreSize, int maxSize, int queueSize) {
        return new ThreadPoolExecutor(coreSize,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize));
    }

    @Deprecated
    public static void stopExecutor(ExecutorService executor) {
        executor.shutdown();
    }

    @Deprecated
    public static void stopExecutor(ExecutorService executor, long timeout, TimeUnit unit) {
        try {
            executor.awaitTermination(timeout, unit);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
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

    public static Properties loadPropsOrDefault(InputStream in) {
        try {
            return loadProps(in);
        } catch (IOException e) {
            return new Properties();
        }
    }

    public static Properties loadPropsOrDefault(String path) {
        try {
            return loadProps(new FileInputStream(path));
        } catch (IOException e) {
            return new Properties();
        }
    }

    public static Properties loadProps(InputStream in) throws IOException {
        Properties p = new Properties();
        p.load(in);
        return p;
    }

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

    public static String wrapWithThreadInfo(String msg) {
        return "[" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + "] " + msg;
    }

    public static String currentThreadName() {
        return "[" + Thread.currentThread().getName() + "] ";
    }

    public static String currentTimePrefix() {
        return DateTimes.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " - ";
    }

    public static long reverseTimestamp() {
        return Long.MAX_VALUE - System.currentTimeMillis();
    }

    public static void addShutdownHook(Runnable target) {
        Runtime.getRuntime().addShutdownHook(new Thread(target));
    }

    public static <E> boolean notEquals(E e1, E e2) {
        return !e1.equals(e2);
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
