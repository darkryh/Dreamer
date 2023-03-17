package com.ead.project.dreamer.domain.apis.discord

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.AccessToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetDiscordUserRefreshToken @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : MutableLiveData<AccessToken?> = getRefreshAccessToken()

    private var refreshAccessToken : MutableLiveData<AccessToken?>?= null

    private fun getRefreshAccessToken() : MutableLiveData<AccessToken?> {
        val discordService = repository.getDiscordService(repository.getDiscordUserRefreshTokenRetrofit())
        val response : Call<AccessToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                try { if (response.isSuccessful) refreshAccessToken?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}", )
            }
        })
        return refreshAccessToken?:MutableLiveData<AccessToken?>().also { refreshAccessToken = it }
    }
}