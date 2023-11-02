package com.ead.project.dreamer.app.model

import com.ead.project.dreamer.app.data.discord.DiscordEAD

data class EadAccount(
    val id : String,
    val typeAccount : TypeAccount,
    val displayName : String,
    val email : String?,
    val profileImage : String?,
    val banner : String?,
    val locale : String?,
    val ranks : List<String>
) {

    val isVip : Boolean get() = run {
        return when(typeAccount) {
            TypeAccount.Discord -> {
                ranks.any { rank -> rank == DiscordEAD.RANK_VIP_ID }
            }
        }
    }

    val ranksNames : List<String> get() = run {
        return when(typeAccount) {
            TypeAccount.Discord -> {
                DiscordEAD.getRanksNames(ranks)
            }
        }
    }
}