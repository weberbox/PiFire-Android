package com.weberbox.pifire.info.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Licenses(
    val list: List<License> = emptyList(),
) : Parcelable {

    @Parcelize
    @Serializable
    data class License(
        val project: String = "",
        val license: String = "",
        val color: String = "#64666666",
        val title: String = "I"
    ) : Parcelable
}
