package com.ead.project.dreamer.domain.apis.discord

import com.ead.project.dreamer.app.data.discord.DiscordEAD
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.discord.GuildMember
import com.ead.project.dreamer.data.retrofit.service.DiscordService
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class GetDiscordUserInToGuild @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke(accessToken : String,userId : String) : Response<GuildMember?> {

        val interceptor = Interceptor { chain ->
            val request: Request

            val tObject = Gson().toJson(AccessObject(accessToken,arrayListOf(DiscordEAD.RANK_USER_ID)))

            val mediaType = "application/json".toMediaType()
            val body = tObject.toString().toRequestBody(mediaType)

            request = chain.request().newBuilder()
                .put(body)
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

        return discordService.getUserIntoGuild(userId).awaitResponse()
    }
}

data class AccessObject(
    val access_token : String,
    val roles : List<String>
)