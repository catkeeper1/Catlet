package org.ckr.catlet.util;

public class LogUtil {
    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void log(String msg, Throwable throwable) {
        System.out.println(msg);
        throwable.printStackTrace(System.out);
    }
}
