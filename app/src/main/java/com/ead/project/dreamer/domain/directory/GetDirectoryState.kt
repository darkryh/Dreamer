package com.ead.project.dreamer.domain.directory

import com.ead.project.dreamer.app.repository.Repository
import com.ead.project.dreamer.domain.PreferenceUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDirectoryState @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    operator fun invoke() : Flow<Boolean> {
        return preferences.getBooleanFlow(Repository.STATE,false)
    }

}