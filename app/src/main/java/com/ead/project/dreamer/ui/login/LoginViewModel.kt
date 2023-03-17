package com.ead.project.dreamer.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.models.discord.AccessToken
import com.ead.project.dreamer.data.models.discord.GuildMember
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val discordUseCase: DiscordUseCase
) : ViewModel() {

    fun getToken() : MutableLiveData<AccessToken?> = discordUseCase.getDiscordUserToken.livedata()

    fun getRefreshToken() : MutableLiveData<AccessToken?> = discordUseCase.getDiscordUserRefreshToken.livedata()

    fun getUserData() : MutableLiveData<User?> = discordUseCase.getDiscordUserData.livedata()

    fun getUserInToGuild(id: String) : MutableLiveData<GuildMember?> = discordUseCase.getDiscordUserInToGuild.livedata(id)
}