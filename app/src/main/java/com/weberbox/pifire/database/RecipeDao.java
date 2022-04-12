package com.weberbox.pifire.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.weberbox.pifire.model.local.RecipesModel;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@Dao
public interface RecipeDao {

    @Query("SELECT * FROM RECIPES ORDER BY ID")
    List<RecipesModel> loadAllRecipes();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecipesModel recipeModel);

    @Update
    void update(RecipesModel recipeModel);

    @Delete
    void delete(RecipesModel recipeModel);

    @Query("SELECT * FROM RECIPES WHERE id = :id")
    RecipesModel loadRecipeById(int id);

    @Query("DELETE FROM RECIPES")
    void nukeTable();

    @RawQuery
    int checkpoint(SupportSQLiteQuery supportSQLiteQuery);
}
