package com.ead.project.dreamer.data.retrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class UserAccessInterceptorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            var request = chain.request()
            val token = "todo" //todo handle the user token

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

