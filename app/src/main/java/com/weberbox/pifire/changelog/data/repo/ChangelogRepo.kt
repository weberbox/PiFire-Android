package com.weberbox.pifire.changelog.data.repo

import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result

interface ChangelogRepo {
    suspend fun getChangelogData(): Result<ChangelogData, DataError>
}