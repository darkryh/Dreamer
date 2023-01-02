package com.ead.project.dreamer.domain.apis.discord

import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.GuildMember
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetDiscordUserInToGuild @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata (id: String) : MutableLiveData<GuildMember?> = getUserInToGuild(id)

    private var guildMember : MutableLiveData<GuildMember?>?= null

    private fun getUserInToGuild(id : String) : MutableLiveData<GuildMember?> {
        val discordService = repository.getDiscordService(repository.getDiscordGuildRetrofit())
        val response : Call<GuildMember?> = discordService.getUserIntoGuild(id)
        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try {
                    if (response.isSuccessful) {
                        guildMember?.value = response.body()
                        DreamerApp.showShortToast("Inicio de Sesión Exitoso!")
                    }
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return guildMember?:MutableLiveData<GuildMember?>().also { guildMember = it }
    }
}