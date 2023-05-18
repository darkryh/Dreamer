package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.discord.DiscordEAD

data class DiscordUser(
    val accent_color: Int,
    val avatar: String?,
    val banner: String?,
    val discriminator: String,
    val email: String,
    val flags: Int,
    val id: String,
    val premium_type: Int,
    val public_flags: Int,
    val username: String,
    val verified: Boolean,
    val rank : String? = DiscordEAD.RANK_USER,
    val level : Int = -1
) {
    companion object {

        fun isVip() : Boolean = false

    }

    fun getAvatarUrl() : String? = if (avatar != null) {
        "${Discord.CDN_ENDPOINT}/avatars/${id}/${avatar}"
    }
    else { null }

    fun getRoles(roleIds : List<String>) : DiscordUser  {
        val level : Int
        if (roleIds.size in 1..1) {

            level = getRankForCorrespondingId(roleIds[0])

        }
        else {

            var idRol = getRankForCorrespondingId(roleIds[0])

            for (pos in 1 until roleIds.size) {
                val currentRol = getRankForCorrespondingId(roleIds[pos])
                if (currentRol < idRol) {
                    idRol = currentRol
                }
            }

            level = idRol

        }

        return copy(
            level = level,
            rank = when(level) {
                5 -> DiscordEAD.RANK_USER
                4 -> DiscordEAD.RANK_MPV
                3 -> DiscordEAD.RANK_VIP
                2 -> DiscordEAD.RANK_ADMIN
                1 -> DiscordEAD.RANK_OWNER
                else -> DiscordEAD.RANK_UNKNOWN
            }
        )
    }

    private fun getRankForCorrespondingId(id : String) : Int {
        return when (id) {
            DiscordEAD.RANK_USER_ID -> 5
            DiscordEAD.RANK_MPV_ID -> 4
            DiscordEAD.RANK_VIP_ID -> 3
            DiscordEAD.RANK_ADMIN_ID -> 2
            DiscordEAD.RANK_OWNER_ID -> 1
            else -> -1
        }
    }

    fun isVip() = rank == DiscordEAD.RANK_VIP
}