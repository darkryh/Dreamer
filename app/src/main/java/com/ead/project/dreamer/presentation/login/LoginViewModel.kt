package com.ead.project.dreamer.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.data.models.discord.GuildMember
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val discordUseCase: DiscordUseCase,
    private val monosChinosUseCase: MonosChinosUseCase,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    private val appBuildPreferences : AppBuildPreferences = preferenceUseCase.appBuildPreferences

    fun getAuthMe(username : String,password : String) = monosChinosUseCase.login.liveData(username, password)

    fun getApplicationState() : Flow<AppBuild> = appBuildPreferences.appBuild

    fun getDiscordToken() : MutableLiveData<DiscordToken?> = discordUseCase.getDiscordUserToken.livedata()

    fun getRefreshToken() : MutableLiveData<DiscordToken?> = discordUseCase.getDiscordUserRefreshToken.livedata()

    fun getDiscordUser() : MutableLiveData<DiscordUser?> = discordUseCase.getDiscordUserData.livedata()

    fun getDiscordUserInToGuild(id: String) : MutableLiveData<GuildMember?> = discordUseCase.getDiscordUserInToGuild.livedata(id)

    fun getGuildMember(id : String) = discordUseCase.getDiscordMember.livedata(id)
}