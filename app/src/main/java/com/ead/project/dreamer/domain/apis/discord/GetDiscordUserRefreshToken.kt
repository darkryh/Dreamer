package com.ead.project.dreamer.domain.apis.discord

import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.data.retrofit.service.DiscordService
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class GetDiscordUserRefreshToken @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(refreshToken : String) : Response<DiscordToken?> {

        val interceptor = Interceptor { chain ->
            var request = chain.request()

            val requestBuilder = request.newBuilder()
            val formBody = FormBody.Builder()
                .add("grant_type","refresh_token")
                .add("client_id", Discord.CLIENT_ID)
                .add("client_secret", Discord.CLIENT_SECRET)
                .add("redirect_uri", Discord.REDIRECT_URI)
                .add("refresh_token",refreshToken)
                .build()

            request = requestBuilder.post(formBody)
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

        return discordService.getAccessToken().awaitResponse()
    }
}