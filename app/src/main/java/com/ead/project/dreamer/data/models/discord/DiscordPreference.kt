package com.ead.project.dreamer.data.models.discord

data class DiscordPreference(
    val discordUser: DiscordUser?,
    val discordToken: DiscordToken?,
    val accessTokenUsed : String?,
    val exchangeCode : String?
)
