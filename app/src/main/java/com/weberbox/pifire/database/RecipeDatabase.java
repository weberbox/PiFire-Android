package com.weberbox.pifire.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.model.local.RecipesModel;

import timber.log.Timber;

@Database(version = 1, entities = {RecipesModel.class}, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d("Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class, Constants.DB_RECIPES)
                        .build();
            }
        }
        Timber.d("Getting the database instance");
        return sInstance;
    }

    public abstract RecipeDao recipeDao();
}
