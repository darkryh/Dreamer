package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class RefreshInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val refreshToken = DataStore.readString(Discord.REFRESH_TOKEN)
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            val formBody = FormBody.Builder()
                .add("client_id", Discord.CLIENT_ID)
                .add("client_secret", Discord.CLIENT_SECRET)
                .add("grant_type","refresh_token")
                .add("refresh_token",refreshToken)
                .build()

            request = requestBuilder.post(formBody)
                .header("Content-Type","application/x-www-form-urlencoded")
                .build()

            chain.proceed(request)

        } catch (e : Exception) {
            e.printStackTrace()
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }

    }
}