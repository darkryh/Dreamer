package com.ead.project.dreamer.ui.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.models.discord.AccessToken
import com.ead.project.dreamer.domain.DiscordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val discordUseCase: DiscordUseCase
) : ViewModel() {

    fun getToken() : MutableLiveData<AccessToken?> = discordUseCase.getDiscordUserToken.livedata()
}