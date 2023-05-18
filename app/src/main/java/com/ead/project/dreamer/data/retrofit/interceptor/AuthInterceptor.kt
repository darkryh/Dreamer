package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.app.data.discord.Discord
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val accessToken = Discord.getDiscordToken()?.access_token

            val request = chain.request().newBuilder()
                .addHeader("Authorization" , "Bearer $accessToken")
                .addHeader("Content-Type" , "application/x-www-form-urlencoded")
                .build()

            chain.proceed(request)

        } catch (e : Exception) {
            e.printStackTrace()
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }
}