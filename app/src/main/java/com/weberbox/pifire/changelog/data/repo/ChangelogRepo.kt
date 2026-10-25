package com.weberbox.pifire.changelog.data.repo

import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result

interface ChangelogRepo {
    suspend fun getChangelogData(): Result<ChangelogData, DataError>
}