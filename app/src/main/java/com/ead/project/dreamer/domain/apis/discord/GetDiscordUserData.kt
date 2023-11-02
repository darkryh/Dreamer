package com.ead.project.dreamer.domain.apis.discord

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.data.retrofit.service.DiscordService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class GetDiscordUserData @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke(accessToken : String) : Response<DiscordUser?> {

        val interceptor = Interceptor { chain ->
            var request = chain.request()

            val requestBuilder = request.newBuilder()

            request = requestBuilder.get()
                .header("Authorization","Bearer $accessToken")
                .header("Content-Type","application/x-www-form-urlencoded")
                .build()

            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = repository.getDiscordRetrofit().newBuilder()
            .client(okHttpClient)
            .build()

        val discordService = retrofit.create(DiscordService::class.java)

        return discordService.getCurrentUser().awaitResponse()
    }
}