package com.ead.project.dreamer.data.models.discord

data class DiscordToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)