package com.ead.project.dreamer.app.data.home

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.model.HomePreference
import com.ead.project.dreamer.data.database.model.ChapterHome
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HomePreferences @Inject constructor(
    private val store: DataStore<HomePreference>
) {

    suspend fun getList() : List<ChapterHome> {
        return store.data.first().list
    }

    suspend fun addToList(list: List<ChapterHome>) {
        store.updateData { homePreference: HomePreference ->
            homePreference.copy(
                list = if (homePreference.list.isEmpty()) {
                    homePreference.list + list
                }
                else {
                    val filteredList : MutableList<ChapterHome> = mutableListOf()
                    list.forEach { chapterHome: ChapterHome ->
                        if (!homePreference.list.contains(chapterHome)) {
                            filteredList.add(chapterHome)
                        }
                    }
                    homePreference.list + filteredList
                }
            )
        }
    }

    suspend fun getNotificationsIndex() : Int {
        return store.data.first().notifyingIndex
    }

    suspend fun setNotificationIndex(index : Int) {
        store.updateData { homePreference: HomePreference ->
            homePreference.copy(
                notifyingIndex = if (index < NOTIFICATION_LIMIT_INDEX) { index }
                else { NOTIFICATION_DEFAULT }
            )
        }
    }

    companion object {
        const val NOTIFICATION_DEFAULT = 0
        const val NOTIFICATION_LIMIT_INDEX = 50
        const val HOME_ITEM_LIMIT = 32
        const val NOTIFICATION_CHANNEL_ID = 700
    }
}