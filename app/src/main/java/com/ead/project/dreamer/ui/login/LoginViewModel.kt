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
    private val discordManager: DiscordManager
) : ViewModel() {

    fun getToken() : MutableLiveData<AccessToken?> = discordManager.getDiscordUserToken.livedata()

    fun getRefreshToken() : MutableLiveData<AccessToken?> = discordManager.getDiscordUserRefreshToken.livedata()

    fun getUserData() : MutableLiveData<User?> = discordManager.getDiscordUserData.livedata()

    fun getUserInToGuild(id: String) : MutableLiveData<GuildMember?> = discordManager.getDiscordUserInToGuild.livedata(id)
}