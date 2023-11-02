package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.app.data.discord.Discord

data class DiscordUser(
    val accent_color: Int,
    val avatar: String?,
    val avatar_decoration_data: String?,
    val banner: String?,
    val banner_color: String?,
    val discriminator: String,
    val email: String?,
    val flags: Int,
    val global_name: String?,
    val id: String,
    val locale: String?,
    val mfa_enabled: Boolean,
    val premium_type: Int,
    val public_flags: Int,
    val username: String,
    val verified: Boolean
) {
    val cdn_avatar get() = if (avatar != null) {
        "${Discord.CDN_ENDPOINT}/avatars/${id}/${avatar}"
    }
    else {
        null
    }

}