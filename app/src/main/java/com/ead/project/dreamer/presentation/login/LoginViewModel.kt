package com.ead.project.dreamer.presentation.login

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val monosChinosUseCase: MonosChinosUseCase,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    private val appBuildPreferences : AppBuildPreferences = preferenceUseCase.appBuildPreferences

    fun getAuthMe(username : String,password : String) = monosChinosUseCase.login.liveData(username, password)

    fun getApplicationState() : Flow<AppBuild> = appBuildPreferences.appBuild

}