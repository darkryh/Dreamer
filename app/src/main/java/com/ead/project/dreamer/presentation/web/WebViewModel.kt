package com.ead.project.dreamer.presentation.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.domain.DiscordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val discordUseCase: DiscordUseCase
) : ViewModel() {

    fun getToken() : MutableLiveData<DiscordToken?> = discordUseCase.getDiscordUserToken.livedata()
}