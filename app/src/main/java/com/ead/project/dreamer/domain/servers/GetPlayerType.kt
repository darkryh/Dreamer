package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.presentation.player.PlayerActivity
import com.ead.project.dreamer.presentation.player.PlayerExternalActivity
import com.ead.project.dreamer.presentation.player.PlayerWebActivity
import javax.inject.Inject

class GetPlayerType @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val playerPreferences = preferenceUseCase.playerPreferences

    operator fun invoke(isDirect : Boolean,chapter : Chapter) : Class<*> {
        playerPreferences.setChapter(chapter)
        return if (isDirect) {
            if (!playerPreferences.isInExternalMode()) {
                playerPreferences.setCastingChapter(chapter)
                PlayerActivity::class.java
            }
            else { PlayerExternalActivity::class.java }
        }
        else { PlayerWebActivity::class.java }
    }
}