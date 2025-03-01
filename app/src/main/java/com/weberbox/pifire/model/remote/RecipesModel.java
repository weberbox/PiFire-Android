package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.utils.adapters.CustomTypeAdapterFactory;

import java.util.List;

@SuppressWarnings("unused")
public class RecipesModel {

    @SerializedName("recipe_details")
    @Expose
    private List<RecipeDetails> recipeDetails;

    public List<RecipeDetails> getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(List<RecipeDetails> recipeDetails) {
        this.recipeDetails = recipeDetails;
    }

    public RecipesModel withRecipeDetails(List<RecipeDetails> recipeDetails) {
        this.recipeDetails = recipeDetails;
        return this;
    }

    public static class RecipeDetails {

        @SerializedName("details")
        @Expose
        private Details details;
        @SerializedName("filename")
        @Expose
        private String filename;
        private boolean isSelected = false;

        public Details getDetails() {
            return details;
        }

        public void setDetails(Details details) {
            this.details = details;
        }

        public RecipeDetails withDetails(Details details) {
            this.details = details;
            return this;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public RecipeDetails withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

    }
    public static class Details {

        @SerializedName("assets")
        @Expose
        private List<Asset> assets;
        @SerializedName("comments")
        @Expose
        private List<Object> comments;
        @SerializedName("metadata")
        @Expose
        private MetaData metadata;
        @SerializedName("recipe")
        @Expose
        private Recipe recipe;

        public List<Asset> getAssets() {
            return assets;
        }

        public void setAssets(List<Asset> assets) {
            this.assets = assets;
        }

        public Details withAssets(List<Asset> assets) {
            this.assets = assets;
            return this;
        }

        public List<Object> getComments() {
            return comments;
        }

        public void setComments(List<Object> comments) {
            this.comments = comments;
        }

        public Details withComments(List<Object> comments) {
            this.comments = comments;
            return this;
        }

        public MetaData getMetadata() {
            return metadata;
        }

        public void setMetadata(MetaData metadata) {
            this.metadata = metadata;
        }

        public Details withMetadata(MetaData metadata) {
            this.metadata = metadata;
            return this;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public void setRecipe(Recipe recipe) {
            this.recipe = recipe;
        }

        public Details withRecipe(Recipe recipe) {
            this.recipe = recipe;
            return this;
        }

    }

    public static class Asset {

        @SerializedName("filename")
        @Expose
        private String filename;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("encoded_image")
        @Expose
        private String encodedImage;
        @SerializedName("encoded_thumb")
        @Expose
        private String encodedThumb;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Asset withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Asset withId(String id) {
            this.id = id;
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Asset withType(String type) {
            this.type = type;
            return this;
        }

        public String getEncodedImage() {
            return encodedImage;
        }

        public void setEncodedImage(String encodedImage) {
            this.encodedImage = encodedImage;
        }

        public Asset withEncodedImage(String encodedImage) {
            this.encodedImage = encodedImage;
            return this;
        }

        public String getEncodedThumb() {
            return encodedThumb;
        }

        public void setEncodedThumb(String encodedThumb) {
            this.encodedThumb = encodedThumb;
        }

        public Asset withEncodedThumb(String encodedThumb) {
            this.encodedThumb = encodedThumb;
            return this;
        }

    }

    public static class MetaData {

        @SerializedName("author")
        @Expose
        private String author;
        @SerializedName("cook_time")
        @Expose
        private Integer cookTime;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("difficulty")
        @Expose
        private String difficulty;
        @SerializedName("food_probes")
        @Expose
        private Integer foodProbes;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("prep_time")
        @Expose
        private Integer prepTime;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("thumb")
        @Expose
        private String thumb;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("units")
        @Expose
        private String units;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("version")
        @Expose
        private String version;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public MetaData withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Integer getCookTime() {
            return cookTime;
        }

        public void setCookTime(Integer cookTime) {
            this.cookTime = cookTime;
        }

        public MetaData withCookTime(Integer cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public MetaData withDescription(String description) {
            this.description = description;
            return this;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public MetaData withDifficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Integer getFoodProbes() {
            return foodProbes;
        }

        public void setFoodProbes(Integer foodProbes) {
            this.foodProbes = foodProbes;
        }

        public MetaData withFoodProbes(Integer foodProbes) {
            this.foodProbes = foodProbes;
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public MetaData withId(String id) {
            this.id = id;
            return this;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public MetaData withImage(String image) {
            this.image = image;
            return this;
        }

        public Integer getPrepTime() {
            return prepTime;
        }

        public void setPrepTime(Integer prepTime) {
            this.prepTime = prepTime;
        }

        public MetaData withPrepTime(Integer prepTime) {
            this.prepTime = prepTime;
            return this;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public MetaData withRating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public MetaData withThumb(String thumb) {
            this.thumb = thumb;
            return this;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public MetaData withThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public MetaData withTitle(String title) {
            this.title = title;
            return this;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public MetaData withUnits(String units) {
            this.units = units;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public MetaData withUsername(String username) {
            this.username = username;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public MetaData withVersion(String version) {
            this.version = version;
            return this;
        }

    }

    public static class Recipe {

        @SerializedName("ingredients")
        @Expose
        private List<Ingredient> ingredients;
        @SerializedName("instructions")
        @Expose
        private List<Instruction> instructions;
        @SerializedName("steps")
        @Expose
        private List<Step> steps;

        public List<Ingredient> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        public Recipe withIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public List<Instruction> getInstructions() {
            return instructions;
        }

        public void setInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
        }

        public Recipe withInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
            return this;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }

        public Recipe withSteps(List<Step> steps) {
            this.steps = steps;
            return this;
        }

    }

    public static class Ingredient {

        @SerializedName("assets")
        @Expose
        private List<String> assets;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("quantity")
        @Expose
        private String quantity;

        public List<String> getAssets() {
            return assets;
        }

        public void setAssets(List<String> assets) {
            this.assets = assets;
        }

        public Ingredient withAssets(List<String> assets) {
            this.assets = assets;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Ingredient withName(String name) {
            this.name = name;
            return this;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public Ingredient withQuantity(String quantity) {
            this.quantity = quantity;
            return this;
        }

    }

    public static class Instruction {

        @SerializedName("assets")
        @Expose
        private List<String> assets;
        @SerializedName("ingredients")
        @Expose
        private List<String> ingredients;
        @SerializedName("step")
        @Expose
        private Integer step;
        @SerializedName("text")
        @Expose
        private String text;

        public List<String> getAssets() {
            return assets;
        }

        public void setAssets(List<String> assets) {
            this.assets = assets;
        }

        public Instruction withAssets(List<String> assets) {
            this.assets = assets;
            return this;
        }

        public List<String> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
        }

        public Instruction withIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Integer getStep() {
            return step;
        }

        public void setStep(Integer step) {
            this.step = step;
        }

        public Instruction withStep(Integer step) {
            this.step = step;
            return this;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Instruction withText(String text) {
            this.text = text;
            return this;
        }

    }

    public static class Step {

        @SerializedName("hold_temp")
        @Expose
        private Integer holdTemp;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("notify")
        @Expose
        private Boolean notify;
        @SerializedName("pause")
        @Expose
        private Boolean pause;
        @SerializedName("timer")
        @Expose
        private Integer timer;
        @SerializedName("trigger_temps")
        @Expose
        private TriggerTemps triggerTemps;
        private Boolean currentStep;

        public Integer getHoldTemp() {
            return holdTemp;
        }

        public void setHoldTemp(Integer holdTemp) {
            this.holdTemp = holdTemp;
        }

        public Step withHoldTemp(Integer holdTemp) {
            this.holdTemp = holdTemp;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Step withMessage(String message) {
            this.message = message;
            return this;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Step withMode(String mode) {
            this.mode = mode;
            return this;
        }

        public Boolean getNotify() {
            return notify;
        }

        public void setNotify(Boolean notify) {
            this.notify = notify;
        }

        public Step withNotify(Boolean notify) {
            this.notify = notify;
            return this;
        }

        public Boolean getPause() {
            return pause;
        }

        public void setPause(Boolean pause) {
            this.pause = pause;
        }

        public Step withPause(Boolean pause) {
            this.pause = pause;
            return this;
        }

        public Integer getTimer() {
            return timer;
        }

        public void setTimer(Integer timer) {
            this.timer = timer;
        }

        public Step withTimer(Integer timer) {
            this.timer = timer;
            return this;
        }

        public TriggerTemps getTriggerTemps() {
            return triggerTemps;
        }

        public void setTriggerTemps(TriggerTemps triggerTemps) {
            this.triggerTemps = triggerTemps;
        }

        public Step withTriggerTemps(TriggerTemps triggerTemps) {
            this.triggerTemps = triggerTemps;
            return this;
        }

        public Boolean getCurrentStep() {
            return currentStep;
        }

        public void setCurrentStep(Boolean currentStep) {
            this.currentStep = currentStep;
        }

    }

    public static class TriggerTemps {

        @SerializedName("food")
        @Expose
        private List<Integer> food;
        @SerializedName("primary")
        @Expose
        private Integer primary;

        public List<Integer> getFood() {
            return food;
        }

        public void setFood(List<Integer> food) {
            this.food = food;
        }

        public TriggerTemps withFood(List<Integer> food) {
            this.food = food;
            return this;
        }

        public Integer getPrimary() {
            return primary;
        }

        public void setPrimary(Integer primary) {
            this.primary = primary;
        }

        public TriggerTemps withPrimary(Integer primary) {
            this.primary = primary;
            return this;
        }

    }

    public static RecipesModel parseJSON(String response) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapterFactory(new CustomTypeAdapterFactory())
                .create();
        return gson.fromJson(response, RecipesModel.class);
    }

}
