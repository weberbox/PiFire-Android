package com.weberbox.pifire.recipes.presentation.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.formatMinutes
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import java.util.Collections
import java.util.LinkedList

internal fun printRecipe(recipe: Recipe, context: Context) {
    val webView = WebView(context)
    val printJobName = recipe.recipeFilename
    if (printJobName.isNotBlank()) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context
                    .getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = webView.createPrintDocumentAdapter(printJobName)
                printManager.print(printJobName, printAdapter, PrintAttributes.Builder().build())
            }
        }

        webView.settings.allowFileAccess = true
        webView.loadDataWithBaseURL(
            "file:///android_asset",
            getRecipeHtml(recipe, context),
            "text/html; charset=utf-8",
            "UTF-8", ""
        )
    }
}

internal fun shareRecipe(recipe: Recipe, context: Context) {
    val sendIntent = Intent()
    sendIntent.setAction(Intent.ACTION_SEND)
    sendIntent.putExtra(Intent.EXTRA_TEXT, getRecipeString(recipe, context))
    sendIntent.setType("text/plain")
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun getRecipeHtml(recipe: Recipe, context: Context): String {
    return "<html lang=en>" +
            HEAD +
            "<body><div id=header><div class=title><h1>" +
            recipe.metadata.title +
            "</h1><div class=rating>" +
            getStarRatingHtml(recipe) +
            "</div></div>" +
            getRecipeImageHtml(recipe) +
            "</div>" +
            "<div class=attr><div><p>" +
            context.getString(R.string.recipes_author) +
            "<p>" +
            recipe.metadata.author +
            "</div><div><p>" +
            context.getString(R.string.recipes_difficulty) +
            "<p>" +
            recipe.metadata.difficulty +
            "</div><div><p>" +
            context.getString(R.string.recipes_prep_time) +
            "<p>" +
            formatMinutes(recipe.metadata.prepTime) +
            "</div><div><p>" +
            context.getString(R.string.recipes_cook_time) +
            "<p>" +
            formatMinutes(recipe.metadata.cookTime) +
            "</div></div><p><div><p><h2>" +
            context.getString(R.string.recipes_description) +
            "</h2><p>" +
            recipe.metadata.description +
            "</div>" +
            getIngredientsHtml(recipe, context) +
            getInstructionsHtml(recipe, context) +
            "</body>" +
            "</html>"
}

private fun getStarRatingHtml(recipe: Recipe): String {
    return TextUtils.join(
        "", Collections.nCopies(
            recipe.metadata.rating,
            RATE
        )
    ) + TextUtils.join(
        "", Collections.nCopies(
            5 - recipe.metadata.rating, UNRATE
        )
    )
}

private fun getRecipeImageHtml(recipe: Recipe): String {
    val format = "<img src=\"data:image/bmp;base64,%s\" alt=\"%s\" />"
    val image: String = recipe.metadata.image
    if (image.isNotEmpty()) {
        for (asset: Recipe.Asset in recipe.assets) {
            if (asset.filename.equals(image, ignoreCase = true)) {
                return String.format(format, asset.encodedImage, asset.filename)
            }
        }
    }
    return ""
}

private fun getIngredientsHtml(recipe: Recipe, context: Context): String {
    val ingredients: MutableList<String?> =
        LinkedList()
    val list: List<Recipe.Ingredient> = recipe.ingredients

    if (list.isNotEmpty()) {
        ingredients.add(
            String.format(
                "<h2>%s</h2><ul>", context.getString(
                    R.string.recipes_ingredients
                )
            )
        )

        for (ingredient: Recipe.Ingredient in list) {
            val recipeItem: Array<String> =
                arrayOf(ingredient.quantity, ingredient.name)
            val value: String = String.format(
                "<li>%s</li>",
                recipeItem.joinToString(separator = " - ")
            )
            ingredients.add(value)
        }
        ingredients.add("</ul>")
        return TextUtils.join("", ingredients)
    } else {
        return ""
    }
}

private fun getInstructionsHtml(recipe: Recipe, context: Context): String {
    val instructions: MutableList<String?> =
        LinkedList()
    val list: List<Recipe.Instruction> =
        recipe.instructions

    if (list.isNotEmpty()) {
        var count = 1
        instructions.add(
            String.format(
                "<h2>%s</h2><ul>", context.getString(
                    R.string.recipes_instructions
                )
            )
        )

        for (instruction: Recipe.Instruction in list) {
            val value: String =
                String.format("<ol>%s - %s</ol>", count, instruction.text)
            count++
            instructions.add(value)
        }
        instructions.add("</ul>")
        return TextUtils.join("", instructions)
    } else {
        return ""
    }
}

private fun getRecipeString(recipe: Recipe, context: Context): String {
    return recipe.metadata.title +
            "\n\n" + String.format(
        "%s ",
        context.getString(R.string.recipes_rating)
    ) +
            (if (recipe.metadata.rating != 0) recipe.metadata.rating else
                context.getString(R.string.placeholder_none)) +
            "\n" + String.format(
        "%s ",
        context.getString(R.string.recipes_prep_time)
    ) +
            formatMinutes(recipe.metadata.prepTime) +
            "\n" + String.format(
        "%s ",
        context.getString(R.string.recipes_cook_time)
    ) +
            formatMinutes(recipe.metadata.cookTime) +
            "\n" + String.format(
        "%s ",
        context.getString(R.string.recipes_difficulty)
    ) +
            recipe.metadata.difficulty +
            "\n\n" + String.format(
        "%s: ",
        context.getString(R.string.recipes_description)
    ) +
            "\n" +
            recipe.metadata.description +
            "\n\n" +
            getIngredientsString(recipe, context) +
            getInstructionsString(recipe, context)
}

private fun getIngredientsString(recipe: Recipe, context: Context): String {
    val ingredients: MutableList<String?> =
        LinkedList()
    val list: List<Recipe.Ingredient> = recipe.ingredients

    if (list.isNotEmpty()) {
        ingredients.add(
            String.format(
                "%s:\n", context.getString(
                    R.string.recipes_ingredients
                )
            )
        )

        for (ingredient: Recipe.Ingredient in list) {
            val recipeItem: Array<String> =
                arrayOf(ingredient.quantity, ingredient.name)
            val value: String = String.format(
                "• %s\n",
                recipeItem.joinToString(separator = " - ")
            )
            ingredients.add(value)
        }
        ingredients.add("\n")
        return TextUtils.join("", ingredients)
    } else {
        return ""
    }
}

private fun getInstructionsString(recipe: Recipe, context: Context): String {
    val instructions: MutableList<String?> =
        LinkedList()
    val list: List<Recipe.Instruction> =
        recipe.instructions

    if (list.isNotEmpty()) {
        var count = 1
        instructions.add(
            String.format(
                "%s:\n", context.getString(
                    R.string.recipes_instructions
                )
            )
        )

        for (instruction: Recipe.Instruction in list) {
            val value: String =
                String.format("%s - %s\n", count, instruction.text)
            count++
            instructions.add(value)
        }
        instructions.add("\n")
        return TextUtils.join("", instructions)
    } else {
        return ""
    }
}

private const val RATE: String = "<svg width=\"100%\" height=\"100%\" viewBox=\"0 0 24 " +
        "24\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"" +
        "http://www.w3.org/1999/xlink\" style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-" +
        "linejoin:round;stroke-miterlimit:2\"><path d=\"M10.756 2.826 8.419 7.98l-5.624.63c-" +
        ".533.06-.982.425-1.147.935-.166.51-.018 1.07.378 1.431l4.179 3.816-1.138 5.543c-" +
        ".108.525.101 1.065.535 1.38.434.315 1.012.348 1.478.083L12 19.002l4.92 2.796c.466." +
        "2651.044.232 1.478-.083.434-.315.643-.855.535-1.38l-1.138-5.543 4.179-3.816c." +
        "396-.361.544-.921.378-1.431-.165-.51-.614-.875-1.147-.935l-5.624-.63-2.337-" +
        "5.154c-.221-.489-.708-.802-1.244-.802s-1.023.313-1.244.802z\"/></svg>"

private const val UNRATE: String = "<svg width=\"100%\" height=\"100%\" viewBox=\"0 0 24 " +
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
        "4.925z\"/></svg>"

private const val HEAD: String = "<head><meta charset=UTF-8><meta content=\"IE=edge\" " +
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
        "{color:inherit}h4{margin-top:.5rem;margin-bottom:.5rem}</style></head>"

