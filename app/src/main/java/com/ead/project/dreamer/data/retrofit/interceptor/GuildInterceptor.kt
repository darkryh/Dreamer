package com.ead.project.dreamer.data.retrofit.interceptor

import android.util.Log
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.discord.DiscordEAD
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response



class GuildInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            var request = chain.request()
            val accessToken = Discord.getDiscordToken()?.access_token?:return chain.proceed(request)
            Log.d("testing", "GuildInterceptor: $accessToken")

            val tObject = Gson().
            toJson(AccessObject(accessToken,arrayListOf(DiscordEAD.BASIC_RANK_ID)))

            val mediaType = "application/json".toMediaType()
            val body = tObject.toString().toRequestBody(mediaType)

            request = chain.request().newBuilder()
                .header("Authorization","Bot ${DiscordEAD.BOT_TOKEN}")
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