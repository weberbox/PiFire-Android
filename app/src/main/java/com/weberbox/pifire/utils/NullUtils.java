package com.weberbox.pifire.utils;

public class NullUtils {

    public static boolean checkObjectNotNull(Object... objects) {
        for (Object o: objects) {
            if (o == null) {
                return false;
            }
        }
        return true;
    }
}
