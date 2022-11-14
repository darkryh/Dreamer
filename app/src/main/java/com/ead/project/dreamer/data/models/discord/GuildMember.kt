package com.ead.project.dreamer.data.models.discord

import java.util.*

data class GuildMember(
    val avatar: String?,
    val communication_disabled_until: Date?,
    val deaf: Boolean,
    val is_pending: Boolean,
    val joined_at: Date,
    val mute: Boolean,
    val nick: String?,
    val pending: Boolean,
    val premium_since: Date?,
    val roles: List<String>,
    val user: UserIn
)