package com.ead.project.dreamer.presentation.update

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.UpdateUseCase
import com.ead.project.dreamer.domain.downloads.EnqueueDownload
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val enqueueDownload: EnqueueDownload,
    val updateUseCase: UpdateUseCase,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    val appBuildPreferences = preferenceUseCase.appBuildPreferences

    fun downloadUpdate(appBuild: AppBuild) : Long {
        return enqueueDownload(appBuild.update,appBuild.downloadReference)
    }
}