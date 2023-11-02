package com.ead.project.dreamer.presentation.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.data.models.discord.SignInResult
import com.ead.project.dreamer.domain.DiscordUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val discordUseCase: DiscordUseCase,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    private val userPreferences = preferenceUseCase.userPreferences
    private val _discordMember : MutableLiveData<SignInResult> = MutableLiveData()

    fun signInDiscord(code : String) : LiveData<SignInResult> {
        viewModelScope.launch {
            _discordMember.postValue(discordUseCase.getDiscordMember(code))
        }
        return _discordMember
    }

    fun login(eadAccount: EadAccount) {
        userPreferences.login(eadAccount = eadAccount)
    }
}