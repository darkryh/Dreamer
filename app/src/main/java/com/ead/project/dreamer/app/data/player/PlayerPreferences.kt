package com.ead.project.dreamer.app.data.player

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.model.PlayerPreference
import com.ead.project.dreamer.app.model.Requester
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PlayerPreferences @Inject constructor(
    private val store: DataStore<PlayerPreference>
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val preference : Flow<PlayerPreference> get() = store.data

    fun isInExternalMode() = runBlocking { store.data.first().isInExternalMode }
    fun isInPictureInPictureMode() = runBlocking { store.data.first().isInPictureInPictureMode }

    fun getChapter() = runBlocking { store.data.first().chapter }
    fun getCastingChapter() = runBlocking { store.data.first().castingChapter }

    fun setChapter(chapter: Chapter) {
        scope.launch {
            store.updateData { playerPreference: PlayerPreference ->
                playerPreference.copy(
                    chapter = chapter
                )
            }
        }
    }

    fun setCastingChapter(chapter: Chapter) {
        scope.launch {
            store.updateData { playerPreference: PlayerPreference ->
                playerPreference.copy(
                    castingChapter = chapter
                )
            }
        }
    }

    fun updateExternalMode() {
        scope.launch {
            store.updateData { playerPreference: PlayerPreference ->
                playerPreference.copy(
                    isInExternalMode = !playerPreference.isInExternalMode
                )
            }
        }
    }

    fun updatePictureInPictureMode() {
        scope.launch {
            store.updateData { playerPreference: PlayerPreference ->
                playerPreference.copy(
                    isInPictureInPictureMode = !playerPreference.isInPictureInPictureMode
                )
            }
        }
    }

    fun setRequestingProfile(requester: Requester) {
        scope.launch {
            store.updateData { playerPreference: PlayerPreference ->
                playerPreference.copy(
                    requester = requester
                )
            }
        }
    }
}