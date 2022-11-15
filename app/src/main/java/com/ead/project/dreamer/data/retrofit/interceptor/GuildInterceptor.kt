package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response



class GuildInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val accessToken = DataStore.readString(Discord.ACCESS_TOKEN)
            val tObject = Gson().
            toJson(AccessObject(accessToken,arrayListOf(Discord.BASIC_RANK_ID)))

            val mediaType = "application/json".toMediaType()
            val body = tObject.toString().toRequestBody(mediaType)

            val request = chain.request().newBuilder()
                .header("Authorization","Bot ${Discord.BOT_TOKEN}")
                .put(body)
                .build()

            chain.proceed(request)

        } catch (e : Exception) {
            e.printStackTrace()
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }

}

data class AccessObject(
    val access_token : String,
    val roles : List<String>
)