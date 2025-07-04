package com.weberbox.pifire.settings.data.util

import androidx.datastore.core.DataStore
import com.weberbox.pifire.dashboard.presentation.model.DashData
import com.weberbox.pifire.events.presentation.model.EventsData
import com.weberbox.pifire.info.presentation.model.InfoData
import com.weberbox.pifire.pellets.presentation.model.PelletsData
import com.weberbox.pifire.recipes.presentation.model.RecipesData
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.presentation.model.SettingsData
import javax.inject.Inject

class ServerDataManager @Inject constructor(
    private val settingsDataStore: DataStore<SettingsData>,
    private val eventsDataStore: DataStore<EventsData>,
    private val recipesDataStore: DataStore<RecipesData>,
    private val dashDataStore: DataStore<DashData>,
    private val pelletsDataStore: DataStore<PelletsData>,
    private val infoDataStore: DataStore<InfoData>,
    private val headersDataStore: DataStore<HeadersData>
) {

    suspend fun deleteServer(uuid: String) {
        settingsDataStore.updateData { data ->
            data.copy(
                serverMap = data.serverMap.filterNot { it.key == uuid }
            )
        }
        eventsDataStore.updateData { data ->
            data.copy(
                eventsMap = data.eventsMap.filterNot { it.key == uuid }
            )
        }
        pelletsDataStore.updateData { data ->
            data.copy(
                pelletsMap = data.pelletsMap.filterNot { it.key == uuid }
            )
        }
        recipesDataStore.updateData { data ->
            data.copy(
                recipesMap = data.recipesMap.filterNot { it.key == uuid }
            )
        }
        dashDataStore.updateData { data ->
            data.copy(
                dashMap = data.dashMap.filterNot { it.key == uuid }
            )
        }
        infoDataStore.updateData { data ->
            data.copy(
                infoMap = data.infoMap.filterNot { it.key == uuid }
            )
        }
        headersDataStore.updateData { data ->
            data.copy(
                headersMap = data.headersMap.filterNot { it.key == uuid }
            )
        }
    }

}