package org.ckr.catlet.jpa.internal;

public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }
}
