package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.weberbox.pifire.constants.Constants;

@SuppressWarnings("unused")
@Entity(tableName = Constants.DB_RECIPES_TABLE)
public class RecipesModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String time;
    private String difficulty;
    private String rating;
    private String pellets;
    private String created;
    private String modified;
    private String image;
    private String notes;
    private String instructions;
    private String ingredients;

    @Ignore
    private boolean isSelected = false;

    @Ignore
    public RecipesModel(@NonNull String name, String time, String difficulty,
                        String rating, String pellets, String created, String modified,
                        String image, String notes, String instructions,
                        String ingredients) {
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
        this.rating = rating;
        this.pellets = pellets;
        this.created = created;
        this.modified = modified;
        this.image = image;
        this.notes = notes;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public RecipesModel(int id, @NonNull String name, String time, String difficulty,
                        String rating, String pellets, String created, String modified,
                        String image, String notes, String instructions,
                        String ingredients) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
        this.rating = rating;
        this.pellets = pellets;
        this.created = created;
        this.modified = modified;
        this.image = image;
        this.notes = notes;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPellets() {
        return pellets;
    }

    public void setPellets(String pellets) {
        this.pellets = pellets;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String date) {
        this.created = date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String date) {
        this.modified = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String link) {
        this.image = link;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @Ignore
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Ignore
    public boolean isSelected() {
        return isSelected;
    }


    public static class RecipeItems {

        @Expose
        private int key;
        @Expose
        private int type;
        @Expose
        private String quantity;
        @Expose
        private String unit;
        @Expose
        private String value;

        public RecipeItems(int key, int type, String quantity, String unit, String value) {
            this.key = key;
            this.type = type;
            this.quantity = quantity;
            this.unit = unit;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
