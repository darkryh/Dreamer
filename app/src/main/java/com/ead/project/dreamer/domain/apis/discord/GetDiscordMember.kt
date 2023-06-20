package com.ead.project.dreamer.domain.apis.discord

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.GuildMember
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class GetDiscordMember @Inject constructor(
    private val repository: AnimeRepository,
    private val retrofit: Retrofit
) {

    fun livedata(id : String) :  MutableLiveData<GuildMember?> = getGuildMember(id)

    private var guildMember : MutableLiveData<GuildMember?>?= null

    private fun getGuildMember(id : String) : MutableLiveData<GuildMember?> {
        val response : Call<GuildMember?> = repository.getDiscordService(retrofit).getGuildMember(id)
        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try { if (response.isSuccessful) guildMember?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}")
            }
        })
        return guildMember?:MutableLiveData<GuildMember?>().also { guildMember = it }
    }
}