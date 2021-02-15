package org.ckr.catlet.jpa.internal.util;

import java.util.Collection;

public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }


    public static String printCollection(Collection objList, int noOfWhiteSpace) {
        if(objList == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for(Object obj : objList) {
            sb.append(genWhiteSpace(noOfWhiteSpace));
            sb.append(obj.toString());
            sb.append("\n");
        }

        sb.append("]\n");
        return sb.toString();
    }

    public static String genWhiteSpace(int noOfWhiteSpace) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < noOfWhiteSpace; i++) {
            result.append(" ");
        }
        return result.toString();
    }
}
