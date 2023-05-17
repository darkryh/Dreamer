package com.ead.project.dreamer.data.retrofit.interceptor

import com.ead.project.dreamer.app.data.discord.Discord
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class AccessInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            var request = chain.request()
            val code = Discord.getExchangeCode()?:return chain.proceed(request)

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