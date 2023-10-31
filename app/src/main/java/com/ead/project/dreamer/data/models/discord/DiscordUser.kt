package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.discord.DiscordEAD

data class DiscordUser(
    val accent_color: Int,
    val avatar: String?,
    val banner: String?,
    val discriminator: String,
    val email: String?,
    val flags: Int,
    val id: String,
    val premium_type: Int,
    val public_flags: Int,
    val username: String,
    val verified: Boolean,
    val ranks : List<String>
) {

    private val stringBuilder = StringBuilder()

    val cdn_avatar get() = if (avatar != null) { "${Discord.CDN_ENDPOINT}/avatars/${id}/${avatar}" }
    else { null }

    val all_ranks : String get() = run {
        ranks.forEach { rankId ->
            stringBuilder.append("${DiscordEAD.rankFromId(rankId)} ")
        }
        val result = stringBuilder.toString()
        stringBuilder.clear()
        return result
    }

    val isVip get() = ranks.any { rank -> rank == DiscordEAD.RANK_VIP_ID }

}