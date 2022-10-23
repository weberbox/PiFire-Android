package com.weberbox.pifire.database;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.ExecutorDatabaseCallback;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.executors.AppExecutors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class DatabaseUtils {

    private static final int BUFFER = 80000;

    public static void exportDatabase(Context context, Uri outputUri,
                                      List<RecipesModel> recipesModel,
                                      ExecutorDatabaseCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() ->
                callback.onExecutorResult(exportDatabase(context, outputUri, recipesModel)));
    }

    public static void importDatabase(Context context, Uri inputUri, RecipeDatabase db,
                                      ExecutorDatabaseCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() ->
                callback.onExecutorResult(importDatabase(context, inputUri, db)));
    }

    private static boolean exportDatabase(Context context, Uri outputUri,
                                          List<RecipesModel> recipesModel) {

        ArrayList<Uri> inputUris = new ArrayList<>();

        for (int i = 0; i < recipesModel.size(); i++) {
            if (recipesModel.get(i).getImage() != null) {
                inputUris.add(Uri.parse(recipesModel.get(i).getImage()));
            }
        }

        try {
            OutputStream os = context.getContentResolver().openOutputStream(outputUri);
            BufferedInputStream origin;

            try (os; ZipOutputStream zos = new ZipOutputStream(os)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type type = new TypeToken<List<RecipesModel>>() {
                }.getType();
                String stdJson = gson.toJson(recipesModel, type);

                ZipEntry jsonEntry = new ZipEntry(Constants.JSON_RECIPES);
                zos.putNextEntry(jsonEntry);
                zos.write(stdJson.getBytes());
                zos.closeEntry();

                if (inputUris.size() > 0) {
                    byte[] data = new byte[BUFFER];

                    for (Uri uri : inputUris) {
                        ZipEntry imgEntry = new ZipEntry(uri.getLastPathSegment());
                        zos.putNextEntry(imgEntry);
                        InputStream is = context.getContentResolver().openInputStream(uri);

                        if (is != null) {
                            origin = new BufferedInputStream(is, BUFFER);

                            int count;

                            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                                zos.write(data, 0, count);
                            }
                            origin.close();
                        }
                        zos.closeEntry();
                    }
                }

            } catch (Exception e) {
                Timber.e(e, "Failed to zip recipe files");
                return false;
            }
        } catch (IOException e) {
            Timber.e(e, "Failed to export recipes db");
            return false;
        }
        return true;
    }

    private static boolean importDatabase(Context context, Uri uri, RecipeDatabase db) {
        FileUtils.clearImgDir(context);
        db.recipeDao().nukeTable();
        String filePath = context.getFilesDir() + "/img/";
        FileUtils.dirChecker(filePath);

        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            ZipInputStream zin = new ZipInputStream(is);
            ZipEntry ze;
            StringBuilder jsonData = new StringBuilder();
            int read;
            byte[] buffer = new byte[1024];

            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals(Constants.JSON_RECIPES)) {
                    while ((read = zin.read(buffer, 0, 1024)) >= 0) {
                        jsonData.append(new String(buffer, 0, read));
                    }
                    if (jsonData.length() > 0) {
                        Type collectionType = new TypeToken<List<RecipesModel>>() {
                        }.getType();
                        List<RecipesModel> recipes = new Gson().fromJson(jsonData.toString(),
                                collectionType);

                        for (RecipesModel recipe : recipes) {
                            recipe.setId(0);
                            db.recipeDao().insert(recipe);
                        }
                    }
                    zin.closeEntry();

                } else {
                    File file = new File(filePath, ze.getName());
                    FileOutputStream fos = new FileOutputStream(file);
                    String canonicalPath = file.getCanonicalPath();
                    if (!canonicalPath.startsWith(filePath)) {
                        throw new SecurityException("Canonical Path: " +
                                canonicalPath + "File Path: " + filePath);
                    }

                    int len;
                    while ((len = zin.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    zin.closeEntry();
                    fos.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            Timber.e(e, "Recipe database import failed");
            return false;
        }
        return true;
    }
}
