package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.data.models.discord.AccessToken
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.models.discord.GuildMember
import com.ead.project.dreamer.data.models.discord.User
import retrofit2.Call
import retrofit2.http.*


interface DiscordService {

    @POST("oauth2/token")
    fun getAccessToken() : Call<AccessToken?>

    @GET("users/@me")
    fun getCurrentUser() : Call<User?>

    @Headers("Authorization: Bot ${Discord.BOT_TOKEN}")
    @GET("guilds/${Discord.SERVER_ID}/members/{id}")
    fun getGuildMember(
        @Path("id") userId:String
    ) : Call<GuildMember?>

    @PUT("guilds/${Discord.SERVER_ID}/members/{id}")
    fun getUserIntoGuild(
        @Path("id") userId:String
    ) : Call<GuildMember?>
}