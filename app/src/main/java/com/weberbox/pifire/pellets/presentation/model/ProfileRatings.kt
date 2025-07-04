package com.weberbox.pifire.pellets.presentation.model

internal enum class ProfileRatings(val rating: Int, val string: String) {
    One(rating = 1, string = "★"),
    Two(rating = 2, string = "★ ★"),
    Three(rating = 3, string = "★ ★ ★"),
    Four(rating = 4, string = "★ ★ ★ ★"),
    Five(rating = 5, string = "★ ★ ★ ★ ★");

    companion object {
        infix fun from(rating: Int): ProfileRatings = ProfileRatings.entries.firstOrNull {
            it.rating == rating
        } ?: One

        infix fun get(rating: String): ProfileRatings = ProfileRatings.entries.firstOrNull {
            it.string == rating
        } ?: One
    }
}