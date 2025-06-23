package com.weberbox.pifire.pellets.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.common.presentation.util.formatDateString
import com.weberbox.pifire.common.presentation.util.formatDateTimeString
import com.weberbox.pifire.pellets.data.model.PelletsDto
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.util.gramsToImperial
import com.weberbox.pifire.pellets.presentation.util.gramsToMetric

object PelletsDtoToDataMapper : Mapper<PelletsDto, Pellets> {
    private val defaults = Pellets()

    override fun map(from: PelletsDto) = Pellets(
        uuid = from.uuid.orEmpty(),
        dateLoaded = from.pellets?.current?.dateLoaded?.let {
            formatDateTimeString(
                input = it,
                inputFormat = "yyyy-MM-dd HH:mm:ss"
            )
        } ?: defaults.dateLoaded,
        usageImperial = gramsToImperial(from.pellets?.current?.estUsage ?: 0.0),
        usageMetric = gramsToMetric(from.pellets?.current?.estUsage ?: 0.0),
        currentPelletId = from.pellets?.current?.pelletId ?: defaults.currentPelletId,
        currentBrand = from.pellets?.profiles?.get(from.pellets.current?.pelletId)?.brand
            ?: defaults.currentBrand,
        currentWood = from.pellets?.profiles?.get(from.pellets.current?.pelletId)?.wood
            ?: defaults.currentWood,
        currentComments = from.pellets?.profiles?.get(from.pellets.current?.pelletId)?.comments
            ?: defaults.currentComments,
        currentRating = from.pellets?.profiles?.get(from.pellets.current?.pelletId)?.rating
            ?: defaults.currentRating,
        logsList = from.pellets?.log?.map { (date, id) ->
            PelletLog(
                pelletID = id,
                pelletName = from.pellets.profiles?.get(id)?.brand + " " + from.pellets.profiles?.get(
                    id
                )?.wood,
                pelletRating = from.pellets.profiles?.get(id)?.rating ?: defaults.currentRating,
                pelletDate = formatDateString(
                    input = date,
                    inputFormat = "yyyy-MM-dd HH:mm:ss"
                ),
                logDate = date
            )
        } ?: listOf(PelletLog()),
        brandsList = from.pellets?.brands ?: defaults.brandsList,
        woodsList = from.pellets?.woods ?: defaults.woodsList,
        profilesList = from.pellets?.profiles?.map { (id, profile) ->
            PelletProfile(
                brand = profile.brand ?: defaults.currentBrand,
                wood = profile.wood ?: defaults.currentWood,
                rating = profile.rating ?: defaults.currentRating,
                comments = profile.comments ?: defaults.currentComments,
                id = id
            )
        } ?: listOf(PelletProfile()),
        hopperLevel = from.pellets?.current?.hopperLevel ?: defaults.hopperLevel
    )
}