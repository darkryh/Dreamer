package com.ead.project.dreamer.data.retrofit.model.discord

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore

data class AccessToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
) {

    fun isTokenUsed() = this.access_token == DataStore.readString(Constants.USED_ACCESS_TOKEN)
}