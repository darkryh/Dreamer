package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.discord.*
import javax.inject.Inject

class DiscordManager @Inject constructor(
    val getDiscordMember: GetDiscordMember,
    val getDiscordUserData: GetDiscordUserData,
    val getDiscordUserInToGuild: GetDiscordUserInToGuild,
    val getDiscordUserRefreshToken: GetDiscordUserRefreshToken,
    val getDiscordUserToken: GetDiscordUserToken
)