package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val accessToken = DataStore.readString(Discord.ACCESS_TOKEN)
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