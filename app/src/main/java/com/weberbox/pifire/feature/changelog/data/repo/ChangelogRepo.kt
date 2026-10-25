package com.weberbox.pifire.feature.changelog.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.feature.changelog.data.model.ChangelogData

interface ChangelogRepo {
    suspend fun getChangelogData(): Result<ChangelogData, DataError>
}