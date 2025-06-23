package com.weberbox.pifire.pellets.presentation.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.domain.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class PelletsData(
    val pelletsMap: Map<Uuid, Pellets> = emptyMap()
) {
    @Immutable
    @Serializable
    data class Pellets(
        val uuid: String = "",
        val dateLoaded: String = "12/21 9:48 PM",
        val usageImperial: String = "0.0 lbs",
        val usageMetric: String = "0.0 kg",
        val currentPelletId: String = "123124141431241",
        val currentBrand: String = "Generic",
        val currentWood: String = "Alder",
        val currentComments: String = "This is a placeholder profile. Alder is generic and used in " +
                "almost all pellets regardless of the wood type indicated on the packaging.  " +
                "It tends to burn consistently and produces a mild smoke.",
        val currentRating: Int = 4,
        val logsList: List<PelletLog> = emptyList(),
        val brandsList: List<String> = listOf("Generic", "Custom"),
        val woodsList: List<String> = listOf(
            "Alder", "Almond", "Apple", "Apricot", "Blend",
            "Competition", "Cherry", "Chestnut", "Hickory", "Lemon", "Maple", "Mesquite",
            "Mulberry", "Nectarine", "Oak", "Orange", "Peach", "Pear", "Plum", "Walnut"
        ),
        val profilesList: List<PelletProfile> = listOf(PelletProfile()),
        val hopperLevel: Int = 0
    ) {
        @Serializable
        data class PelletProfile(
            val brand: String = "Generic",
            val wood: String = "Alder",
            val rating: Int = 4,
            val comments: String = "",
            val id: String = "123124141431241"
        )

        @Serializable
        data class PelletLog(
            val pelletID: String = "123124141431241",
            val pelletName: String = "Generic Alder",
            val pelletRating: Int = 4,
            val pelletDate: String = "12-21",
            val logDate: String = "2023-12-09 18:24:03"
        )
    }
}
