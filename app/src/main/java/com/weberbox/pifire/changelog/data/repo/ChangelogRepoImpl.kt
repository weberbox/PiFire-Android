package com.weberbox.pifire.changelog.data.repo

import com.weberbox.pifire.R
import com.weberbox.pifire.changelog.data.model.ChangelogDto
import com.weberbox.pifire.changelog.domain.ChangelogDtoToDataMapper
import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.util.readRawJsonFile
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class ChangelogRepoImpl @Inject constructor(
    private val json: Json
) : ChangelogRepo {

    override suspend fun getChangelogData(): Result<ChangelogData, DataError> {
        try {
            val jsonString = readRawJsonFile(R.raw.changelog)
            val changelogDto = json.decodeFromString<ChangelogDto>(jsonString)
            val changelogData = ChangelogDtoToDataMapper.map(changelogDto)

            return Result.Success(changelogData)

        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e, "Changelog JSON Error")
        }
        return Result.Error(DataError.Local.JSON_ERROR)
    }

}