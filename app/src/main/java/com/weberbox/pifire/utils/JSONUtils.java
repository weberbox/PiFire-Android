package com.weberbox.pifire.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JSONUtils {

    private static String gsonJSON(Object object) {
        Gson gson = new Gson();
        @SuppressWarnings("rawtypes")
        Type gsonType = new TypeToken<HashMap>(){}.getType();
        return gson.toJson(object, gsonType);
    }

    public static String encodeJSON(String outerKey, String key, String value) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(key, value);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key, boolean value) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Boolean> innerMap = new HashMap<>();
        innerMap.put(key, value);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    @SuppressWarnings("unused")
    public static String encodeJSON(String outerKey, String key1, String value1, String key2,
                                    String value2) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key1, boolean value1, String key2,
                                    String value2) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    @SuppressWarnings("unused")
    public static String encodeJSON(String outerKey, String key1, String value1, String key2,
                                    String value2, String key3, String value3) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key1, boolean value1, String key2,
                                    String value2, String key3, boolean value3) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    @SuppressWarnings("unused")
    public static String encodeJSON(String outerKey, String key1, boolean value1, String key2,
                                    String value2, String key3, String value3) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key1, String value1, String key2,
                                    String value2, String key3, String value3, String key4,
                                    String value4) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        innerMap.put(key4, value4);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key1, boolean value1, String key2,
                                    String value2, String key3, String value3, String key4,
                                    boolean value4) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        innerMap.put(key4, value4);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }

    public static String encodeJSON(String outerKey, String key1, String value1, String key2,
                                    String value2, String key3, String value3, String key4,
                                    String value4, String key5, String value5) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put(key1, value1);
        innerMap.put(key2, value2);
        innerMap.put(key3, value3);
        innerMap.put(key4, value4);
        innerMap.put(key5, value5);
        outerMap.put(outerKey, innerMap);

        return gsonJSON(outerMap);
    }
}
