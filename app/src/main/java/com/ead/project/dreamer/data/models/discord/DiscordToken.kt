package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.app.data.discord.Discord

data class DiscordToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
) {
    fun isAccessTokenUsed() : Boolean {
        return Discord.isCurrentlyDiscordTokenUsed()
    }

    fun notInitialized() : Boolean {
        return Discord.isDiscordTokensNotInitialized()
    }
}