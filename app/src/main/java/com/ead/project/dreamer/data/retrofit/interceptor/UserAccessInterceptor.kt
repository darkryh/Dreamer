package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import okhttp3.Interceptor
import okhttp3.Response

class UserAccessInterceptorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val token = DataStore.readString(Discord.USER_TOKEN)
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            request = requestBuilder
                .header("Authorization",token)
                .build()

            chain.proceed(request)
        } catch (e : Exception) {
            e.printStackTrace()
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }
}

