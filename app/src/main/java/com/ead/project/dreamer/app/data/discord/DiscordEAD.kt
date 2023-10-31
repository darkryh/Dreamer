package com.ead.project.dreamer.app.data.discord

import com.ead.project.dreamer.data.models.discord.DISCORD_BOT_TOKEN

object DiscordEAD {

    const val SERVER_ID = "934335404172275732"
    const val BASIC_RANK_ID = "946849009262280734"

    const val BOT_TOKEN = DISCORD_BOT_TOKEN


    const val RANK_UNKNOWN = "Unknown"
    const val RANK_USER = "User"
    const val RANK_MPV = "Mvp"
    const val RANK_VIP = "Vip"
    const val RANK_ADMIN = "Admin"
    const val RANK_OWNER = "Owner"

    const val RANK_USER_ID = "946849009262280734"
    const val RANK_MPV_ID = "953071633239785492"
    const val RANK_VIP_ID = "953071933971374141"
    const val RANK_ADMIN_ID = "934336329070833674"
    const val RANK_OWNER_ID = "952259476864499782"

    fun rankFromId(id : String) : String {
        return when(id) {
            RANK_USER_ID -> RANK_USER
            RANK_MPV_ID -> RANK_MPV
            RANK_VIP_ID -> RANK_VIP
            RANK_ADMIN_ID -> RANK_ADMIN
            RANK_OWNER_ID -> RANK_OWNER
            else -> RANK_UNKNOWN
        }
    }
}