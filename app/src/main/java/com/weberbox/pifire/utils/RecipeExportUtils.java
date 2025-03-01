package com.weberbox.pifire.utils;

import android.content.Context;
import android.text.TextUtils;

import com.weberbox.pifire.R;
import com.weberbox.pifire.model.remote.RecipesModel.Asset;
import com.weberbox.pifire.model.remote.RecipesModel.Details;
import com.weberbox.pifire.model.remote.RecipesModel.Ingredient;
import com.weberbox.pifire.model.remote.RecipesModel.Instruction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RecipeExportUtils {

    private final Context context;
    private final Details recipe;

    public RecipeExportUtils(Context context, Details recipe) {
        this.context = context;
        this.recipe = recipe;
    }

    public String getRecipeString() {
        return recipe.getMetadata().getTitle() +
                "\n\n" +
                String.format("%s ", context.getString(R.string.recipes_rating)) +
                (recipe.getMetadata().getRating() != 0 ? recipe.getMetadata().getRating() :
                        context.getString(R.string.placeholder_none)) +
                "\n" +
                String.format("%s ", context.getString(R.string.recipes_prep_time)) +
                TimeUtils.formatMinutes(recipe.getMetadata().getPrepTime()) +
                "\n" +
                String.format("%s ", context.getString(R.string.recipes_cook_time)) +
                TimeUtils.formatMinutes(recipe.getMetadata().getCookTime()) +
                "\n" +
                String.format("%s ", context.getString(R.string.recipes_difficulty)) +
                recipe.getMetadata().getDifficulty() +
                "\n\n" +
                String.format("%s: ", context.getString(R.string.recipes_description)) +
                "\n" +
                recipe.getMetadata().getDescription() +
                "\n\n" +
                getIngredientsString() +
                getInstructionsString();
    }

    private String getIngredientsString() {
        List<String> ingredients = new LinkedList<>();
        List<Ingredient> list = recipe.getRecipe().getIngredients();

        if (list != null && !list.isEmpty()) {
            ingredients.add(String.format("%s:\n", context.getString(
                    R.string.recipes_ingredients)));

            for (Ingredient ingredient : list) {
                String[] recipeItem = {ingredient.getQuantity(), ingredient.getName()};
                String value = String.format("• %s\n",
                        StringUtils.cleanStrings(recipeItem, " "));
                ingredients.add(value);
            }
            ingredients.add("\n");
            return TextUtils.join("", ingredients);
        } else {
            return "";
        }
    }

    private String getInstructionsString() {
        List<String> instructions = new LinkedList<>();
        List<Instruction> list = recipe.getRecipe().getInstructions();

        if (list != null && !list.isEmpty()) {
            int count = 1;
            instructions.add(String.format("%s:\n", context.getString(
                    R.string.recipes_instructions)));

            for (Instruction instruction : list) {
                String value = String.format("%s - %s\n", count, instruction.getText());
                count++;
                instructions.add(value);
            }
            instructions.add("\n");
            return TextUtils.join("", instructions);
        } else {
            return "";
        }
    }

    public String getRecipeHTML() {
        return "<html lang=en>" +
                head +
                "<body><div id=header><div class=title><h1>" +
                recipe.getMetadata().getTitle() +
                "</h1><div class=rating>" +
                getStarRatingHtml() +
                "</div></div>" +
                getImageHtml() +
                "</div>" +
                "<div class=attr><div><p>" +
                context.getString(R.string.recipes_author) +
                "<p>" +
                recipe.getMetadata().getAuthor() +
                "</div><div><p>" +
                context.getString(R.string.recipes_difficulty) +
                "<p>" +
                recipe.getMetadata().getDifficulty() +
                "</div><div><p>" +
                context.getString(R.string.recipes_prep_time) +
                "<p>" +
                TimeUtils.formatMinutes(recipe.getMetadata().getPrepTime()) +
                "</div><div><p>" +
                context.getString(R.string.recipes_cook_time) +
                "<p>" +
                TimeUtils.formatMinutes(recipe.getMetadata().getCookTime()) +
                "</div></div><p><div><p><h2>" +
                context.getString(R.string.recipes_description) +
                "</h2><p>" +
                recipe.getMetadata().getDescription() +
                "</div>" +
                getIngredientsHtml() +
                getInstructionsHtml() +
                "</body>" +
                "</html>";
    }

    private String getStarRatingHtml() {
        return TextUtils.join("", Collections.nCopies(recipe.getMetadata().getRating(),
                rate)) + TextUtils.join("", Collections.nCopies(5 -
                recipe.getMetadata().getRating(), unrate));
    }

    private String getImageHtml() {
        String format = "<img src=\"data:image/bmp;base64,%s\" alt=\"%s\" />";
        String image = recipe.getMetadata().getImage();
        if (image != null && !image.isEmpty()) {
            for (Asset asset : recipe.getAssets()) {
                if (asset.getFilename().equalsIgnoreCase(image)) {
                    return String.format(format, asset.getEncodedImage(), asset.getFilename());
                }
            }
        }
        return "";
    }

    private String getIngredientsHtml() {
        List<String> ingredients = new LinkedList<>();
        List<Ingredient> list = recipe.getRecipe().getIngredients();

        if (list != null && !list.isEmpty()) {
            ingredients.add(String.format("<h2>%s</h2><ul>", context.getString(
                    R.string.recipes_ingredients)));

            for (Ingredient ingredient : list) {
                String[] recipeItem = {ingredient.getQuantity(), ingredient.getName()};
                String value = String.format("<li>%s</li>",
                        StringUtils.cleanStrings(recipeItem, " "));
                ingredients.add(value);
            }
            ingredients.add("</ul>");
            return TextUtils.join("", ingredients);
        } else {
            return "";
        }
    }

    private String getInstructionsHtml() {
        List<String> instructions = new LinkedList<>();
        List<Instruction> list = recipe.getRecipe().getInstructions();

        if (list != null && !list.isEmpty()) {
            int count = 1;
            instructions.add(String.format("<h2>%s</h2><ul>", context.getString(
                    R.string.recipes_instructions)));

            for (Instruction instruction : list) {
                String value = String.format("<ol>%s - %s</ol>", count, instruction.getText());
                count++;
                instructions.add(value);
            }
            instructions.add("</ul>");
            return TextUtils.join("", instructions);
        } else {
            return "";
        }
    }

    private static final String rate = "<svg width=\"100%\" height=\"100%\" viewBox=\"0 0 24 " +
            "24\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"" +
            "http://www.w3.org/1999/xlink\" style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-" +
            "linejoin:round;stroke-miterlimit:2\"><path d=\"M10.756 2.826 8.419 7.98l-5.624.63c-" +
            ".533.06-.982.425-1.147.935-.166.51-.018 1.07.378 1.431l4.179 3.816-1.138 5.543c-" +
            ".108.525.101 1.065.535 1.38.434.315 1.012.348 1.478.083L12 19.002l4.92 2.796c.466." +
            "2651.044.232 1.478-.083.434-.315.643-.855.535-1.38l-1.138-5.543 4.179-3.816c." +
            "396-.361.544-.921.378-1.431-.165-.51-.614-.875-1.147-.935l-5.624-.63-2.337-" +
            "5.154c-.221-.489-.708-.802-1.244-.802s-1.023.313-1.244.802z\"/></svg>";

    private static final String unrate = "<svg width=\"100%\" height=\"100%\" viewBox=\"0 0 24 " +
            "24\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"" +
            "http://www.w3.org/1999/xlink\" style=\"fill-rule:evenodd;clip-rule:evenodd;" +
            "stroke-linejoin:round;stroke-miterlimit:2\"><path d=\"M10.756 2.826 8.419 " +
            "7.98l-5.624.63c-.533.06-.982.425-1.147.935-.166.51-.018 1.07.378 1.431l4.179 " +
            "3.816-1.138 5.543c-.108.525.101 1.065.535 1.38.434.315 1.012.348 1.478.083L12 " +
            "19.002l4.92 2.796c.466.265 1.044.232 1.478-.083.434-.315.643-.855.535-1.38l-1.138-" +
            "5.543 4.179-3.816c.396-.361.544-.921.378-1.431-.165-.51-.614-.875-1.147-.935l-" +
            "5.624-.63-2.337-5.154c-.221-.489-.708-.802-1.244-.802s-1.023.313-1.244.802zM12 " +
            "4.925l1.994 4.398c.146.321.45.542.8.581l4.799.538-3.567 3.256c-.26.237-.376.594-" +
            ".305.94l.972 4.73-4.199-2.386c-.306-.174-.682-.174-.988.0l-4.199 2.386.972-4.73c." +
            "071-.346-.045-.703-.305-.94l-3.567-3.256 4.799-.538c.35-.039.654-.26.8-.581L12 " +
            "4.925z\"/></svg>";

    private static final String head = "<head><meta charset=UTF-8><meta content=\"IE=edge\" " +
            "http-equiv=X-UA-Compatible><meta content=\"width=device-width,initial-scale=1\" " +
            "name=viewport><title>PiFire - Recipe for Print</title><style>a,body,div,html,img," +
            "ol,p,span,ul{border:0;font-size:100%;font:inherit;margin:0;padding:0;vertical-" +
            "align:baseline}@font-face{font-family:Aller-Regular;src:url(/font/aller-regular.ttf)" +
            "}@font-face{font-family:Aller-Bold;src:url(/font/aller-bold.ttf )}body{font-family:" +
            "Aller-Regular,sans-serif;line-height:1.5;max-width:45rem;padding:1.5rem}body>" +
            "p{padding:.5rem 0}#header{display:grid;grid-column-gap:2rem;grid-" +
            "template-columns:1fr auto;margin-bottom:2.5rem;width:100%}img{border-radius:" +
            "1rem;height:8rem;object-fit:cover;width:8rem}h1{font-size:2.25rem;line-height:" +
            "1.25;margin:0;padding-bottom:1rem}svg{width:2rem;height:2rem;padding:0 .5rem " +
            "0 0}h2{margin:1.5rem 0 1rem}.attr{display:grid;grid-column-gap:2rem;grid-template" +
            "-columns:1fr 1fr;margin-top:1rem}.attr>div>p:first-child{font-size:.9rem;" +
            "opacity:.5}ul{list-style-position:inside;padding:0}ol,li{padding:.5rem}a" +
            "{color:inherit}h4{margin-top:.5rem;margin-bottom:.5rem}</style></head>";
}