package com.ead.project.dreamer.domain.apis.discord

import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetDiscordUserData @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () :  MutableLiveData<User?> = getUserData()

    private var user : MutableLiveData<User?>?= null

    private fun getUserData() : MutableLiveData<User?> {
        user?.value = null
        val discordService = repository.getDiscordService(repository.getDiscordAuthRetrofit())
        val response : Call<User?> = discordService.getCurrentUser()
        response.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                try { if (response.isSuccessful) user?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<User?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return user?:MutableLiveData<User?>().also { user = it }
    }
}