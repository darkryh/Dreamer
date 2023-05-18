package com.ead.project.dreamer.domain.apis.discord

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.DiscordToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetDiscordUserRefreshToken @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : MutableLiveData<DiscordToken?> = getRefreshAccessToken()

    private var refreshDiscordToken : MutableLiveData<DiscordToken?>?= null

    private fun getRefreshAccessToken() : MutableLiveData<DiscordToken?> {
        val discordService = repository.getDiscordService(repository.getDiscordUserRefreshTokenRetrofit())
        val response : Call<DiscordToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<DiscordToken?> {
            override fun onResponse(call: Call<DiscordToken?>, response: Response<DiscordToken?>) {
                try { if (response.isSuccessful) refreshDiscordToken?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<DiscordToken?>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}", )
            }
        })
        return refreshDiscordToken?:MutableLiveData<DiscordToken?>().also { refreshDiscordToken = it }
    }
}