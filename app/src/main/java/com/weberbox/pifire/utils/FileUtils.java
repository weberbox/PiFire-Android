package com.weberbox.pifire.utils;

import android.content.Context;

import androidx.annotation.RawRes;

import com.weberbox.pifire.interfaces.ExecutorCallback;
import com.weberbox.pifire.utils.executors.AppExecutors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    private static int retries = 0;

    public static void executorSaveJSON(Context context, String filename, String jsonString) {
        AppExecutors.getInstance().diskIO().execute(() ->
                saveJSONFile(context, filename, jsonString));
    }

    public static void executorLoadJSON(Context context, String filename, ExecutorCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() ->
                callback.onDataLoaded(loadJSONFile(context, filename)));
    }

    public static void executorLoadRawJson(Context context, int file, ExecutorCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() ->
                callback.onDataLoaded(readRawJSONFile(context, file)));
    }

    public static String getJSONString(Context context, String filename) {
        return loadJSONFile(context, filename);
    }

    private static void saveJSONFile(Context context, String filename, String jsonString) {
        if (context != null) {
            boolean isFileCreated = FileUtils.createJSONFile(context,
                    filename, jsonString);
            if (!isFileCreated && retries < 3) {
                // Try 3 times
                retries++;
                saveJSONFile(context, filename, jsonString);
            } else {
                retries = 0;
            }
        }
    }

    private static String loadJSONFile(Context context, String filename) {
        if (context != null) {
            boolean isFilePresent = isFilePresent(context, filename);
            if (isFilePresent) {
                return readJSONFile(context, filename);
            }
        }
        return null;
    }

    private static String readRawJSONFile(Context context, @RawRes int file) {
        try {
            InputStream fis = context.getResources().openRawResource(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            isr.close();
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String readJSONFile(Context context, String fileName) {
        try {
            FileInputStream fis = new FileInputStream(new File(context.getCacheDir(), fileName));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            isr.close();
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean createJSONFile(Context context, String fileName, String jsonString) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(context.getCacheDir(), fileName));
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (IOException fileNotFound) {
            return false;
        }
    }

    private static boolean isFilePresent(Context context, String fileName) {
        String path = context.getCacheDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }
}
