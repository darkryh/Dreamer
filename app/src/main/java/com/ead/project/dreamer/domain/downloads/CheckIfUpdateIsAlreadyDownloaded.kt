package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class CheckIfUpdateIsAlreadyDownloaded @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    operator fun invoke(): Boolean = appBuildPreferences.getLastVersionFile().exists()
}