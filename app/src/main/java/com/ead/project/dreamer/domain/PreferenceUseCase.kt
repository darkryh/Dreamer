package com.ead.project.dreamer.domain

import com.ead.project.dreamer.app.data.action.ActionStore
import com.ead.project.dreamer.app.data.ads.AdPreferences
import com.ead.project.dreamer.app.data.files.FilesPreferences
import com.ead.project.dreamer.app.data.home.HomePreferences
import com.ead.project.dreamer.app.data.player.PlayerPreferences
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.data.preference.EadPreferences
import com.ead.project.dreamer.app.data.preference.Preferences

data class PreferenceUseCase(
    val appBuildPreferences: AppBuildPreferences,
    val actionStore: ActionStore,
    val preferences: Preferences,
    val adPreferences: AdPreferences,
    val homePreferences: HomePreferences,
    val filesPreferences: FilesPreferences,
    val playerPreferences: PlayerPreferences,
    val userPreferences: EadPreferences
)