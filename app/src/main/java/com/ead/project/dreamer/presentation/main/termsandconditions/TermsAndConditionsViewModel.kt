package com.ead.project.dreamer.presentation.main.termsandconditions

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.model.GoogleBuild
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val appBuildPreferences: AppBuildPreferences
) : ViewModel() {

    fun acceptContract() {
        appBuildPreferences.setGoogleBuild(
            GoogleBuild(
                isTermsAndConditionsAccepted = true
            )
        )
    }
}