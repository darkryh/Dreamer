package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import okhttp3.*

class AccessInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val code = DataStore.readString(Discord.EXCHANGE_CODE)
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            val formBody = FormBody.Builder()
                .add("client_id", Discord.CLIENT_ID)
                .add("client_secret", Discord.CLIENT_SECRET)
                .add("grant_type","authorization_code")
                .add("code",code)
                .add("redirect_uri", Discord.REDIRECT_URI)
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