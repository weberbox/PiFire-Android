package com.weberbox.pifire.utils;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    private static int mRetries = 0;

    public static void saveJSONFile(Activity activity, String filename, String jsonString) {
        if (activity != null) {
            boolean isFileCreated = FileUtils.createJSONFile(activity,
                    filename, jsonString);
            if (!isFileCreated && mRetries < 3) {
                // Try 3 times
                mRetries++;
                saveJSONFile(activity, jsonString, filename);
            } else {
                mRetries = 0;
            }
        }
    }

    public static String loadJSONFile(Activity activity, String filename) {
        if (activity != null) {
            boolean isFilePresent = FileUtils.isFilePresent(activity, filename);
            if (isFilePresent) {
                return FileUtils.readJSONFile(activity, filename);
            }
        }
        return null;
    }

    public static String readRawJSONFile(Context context, int file) {
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
