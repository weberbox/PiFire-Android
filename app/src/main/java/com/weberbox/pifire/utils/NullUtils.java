package com.weberbox.pifire.utils;

public class NullUtils {

    public static boolean isAnyObjectNull(Object... objects) {
        for (Object o: objects) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }
}
