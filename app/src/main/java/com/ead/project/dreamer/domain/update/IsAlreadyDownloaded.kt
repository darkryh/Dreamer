package com.ead.project.dreamer.domain.update

import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class IsAlreadyDownloaded @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    operator fun invoke(): Boolean = appBuildPreferences.getLastVersionFile().exists()
}