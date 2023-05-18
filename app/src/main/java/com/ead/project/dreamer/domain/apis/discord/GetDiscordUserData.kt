package com.ead.project.dreamer.domain.apis.discord

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.DiscordUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetDiscordUserData @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () :  MutableLiveData<DiscordUser?> = getUserData()

    private var discordUser : MutableLiveData<DiscordUser?>?= null

    private fun getUserData() : MutableLiveData<DiscordUser?> {
        discordUser?.value = null
        val discordService = repository.getDiscordService(repository.getDiscordAuthRetrofit())
        val response : Call<DiscordUser?> = discordService.getCurrentUser()
        response.enqueue(object : Callback<DiscordUser?> {
            override fun onResponse(call: Call<DiscordUser?>, response: Response<DiscordUser?>) {
                try { if (response.isSuccessful) discordUser?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<DiscordUser?>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}", )
            }
        })
        return discordUser?:MutableLiveData<DiscordUser?>().also { discordUser = it }
    }
}