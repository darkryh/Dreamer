package com.ead.project.dreamer.domain.directory

import com.ead.project.dreamer.app.repository.Repository
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class SetDirectoryState @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    suspend operator fun invoke(value : Boolean) {
        preferences.set(Repository.STATE,value)
    }
}