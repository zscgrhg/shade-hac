package org.apache.http.client.fluent;

public class HacStringBuddy {

    public static String trim(String str) {
        if (str != null) {
            return str.trim();
        } else {
            return null;
        }
    }

    public static String trimBeginSlash(String path) {
        return path.replace("^/+", "");
    }

    public static String join(String sp, Object... data) {
        StringBuilder sb = new StringBuilder();
        for (Object datum : data) {
            sb
                    .append(String.valueOf(datum))
                    .append(sp);
        }
        return sb.toString();
    }
}
