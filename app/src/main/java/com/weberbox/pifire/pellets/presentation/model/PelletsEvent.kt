package com.weberbox.pifire.pellets.presentation.model

import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile

sealed class PelletsEvent {
    data object HopperCheck : PelletsEvent()
    data class AddBrand(val brand: String) : PelletsEvent()
    data class DeleteBrand(val brand: String) : PelletsEvent()
    data class AddWood(val wood: String) : PelletsEvent()
    data class DeleteWood(val wood: String) : PelletsEvent()
    data class DeleteLog(val logDate: String) : PelletsEvent()
    data class AddProfile(val profile: PelletProfile, val load: Boolean) : PelletsEvent()
    data class LoadProfile(val profile: String) : PelletsEvent()
    data class EditProfile(val profile: PelletProfile) : PelletsEvent()
    data class DeleteProfile(val profile: String) : PelletsEvent()
}