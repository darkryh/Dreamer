package com.ead.project.dreamer.data.models.discord

data class UserIn(
    val accent_color: String?,
    val avatar: String?,
    val avatar_decoration_data: String?,
    val banner: String?,
    val banner_color: String?,
    val discriminator: String,
    val flags: Int,
    val global_name: String,
    val id: String,
    val public_flags: Int,
    val premium_type: Int,
    val username: String
)