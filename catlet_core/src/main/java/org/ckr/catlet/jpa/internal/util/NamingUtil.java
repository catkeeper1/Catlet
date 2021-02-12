package org.ckr.catlet.jpa.internal.util;

public class NamingUtil {

    public static String convertPascalCaseToCamelCase(String str) {
        if(str.length() == 1) {
            return str.toLowerCase();
        }

        if (Character.isUpperCase(str.charAt(0)) &&
            Character.isUpperCase(str.charAt(1))) {
            return str;
        }

        StringBuilder result = new StringBuilder();

        result.append(String.valueOf(str.charAt(0)).toLowerCase());
        result.append(str.substring(1));
        return result.toString();
    }

    public static String convertCamelCaseToUnderScoreCase(String str) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < str.length(); i++ ) {
            char ch = str.charAt(i);

            result.append(Character.toUpperCase(ch));

            if(Character.isLowerCase(ch) &&
                    (i + 1) < str.length() &&
                    Character.isUpperCase(str.charAt(i + 1))) {

                result.append('_');
            }
        }

        return result.toString();
    }
}
