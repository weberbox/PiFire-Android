package com.weberbox.pifire.feature.pellets.presentation.model

import com.weberbox.pifire.core.common.model.Brand
import com.weberbox.pifire.core.common.model.Wood
import kotlinx.serialization.Serializable

@Serializable
internal data class ProfilesData(
    val brands: List<Brand>,
    val woods: List<Wood>,
    val id: String = "",
    val currentBrand: String = "",
    val currentWood: String = "",
    val rating: Int = 5,
    val comments: String = ""
)
