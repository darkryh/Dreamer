package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.app.data.discord.DiscordEAD
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.data.models.discord.GuildMember
import retrofit2.Call
import retrofit2.http.*


interface DiscordService {

    @POST("oauth2/token")
    fun getAccessToken() : Call<DiscordToken?>

    @GET("users/@me")
    fun getCurrentUser() : Call<DiscordUser?>

    @Headers("Authorization: Bot ${DiscordEAD.BOT_TOKEN}")
    @GET("guilds/${DiscordEAD.SERVER_ID}/members/{id}")
    fun getGuildMember(
        @Path("id") userId:String
    ) : Call<GuildMember?>

    @PUT("guilds/${DiscordEAD.SERVER_ID}/members/{id}")
    fun getUserIntoGuild(
        @Path("id") userId:String
    ) : Call<GuildMember?>
}