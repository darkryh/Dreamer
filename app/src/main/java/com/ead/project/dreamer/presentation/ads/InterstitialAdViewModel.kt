package com.ead.project.dreamer.presentation.ads

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.domain.PreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InterstitialAdViewModel @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    private val adPreferences = preferenceUseCase.adPreferences

    fun resetViews() {
        adPreferences.resetViews()
    }
}