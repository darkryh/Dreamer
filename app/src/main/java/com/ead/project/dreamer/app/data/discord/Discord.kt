package com.ead.project.dreamer.app.data.discord

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.data.models.discord.DiscordPreference
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.data.models.discord.DiscordUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Discord {

    //context injected by app Instance
    private val context : Context by lazy { App.Instance }
    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // dataStore to handle the DiscordUser class
    private val store = DataStoreFactory.create(
        serializer = DiscordUserSerializer,
        produceFile = { context.dataStoreFile(DISCORD_USER) },
        corruptionHandler = null
    )

    // DiscordUser Instance
    fun getUser() : DiscordUser? = runBlocking { store.data.first().discordUser }

    private val discordPreference : Flow<DiscordPreference>
        get() = store.data

    val user : Flow<DiscordUser?>
        get() = discordPreference.map { discordPreference ->
            discordPreference.discordUser
        }

    fun setExchangeCode(exchangeCode : String) = runBlocking {
        store.updateData { discordPreference: DiscordPreference ->
            discordPreference.copy(
                exchangeCode = exchangeCode
            )
        }
    }

    fun setDiscordToken(discordToken: DiscordToken) = runBlocking {
        store.updateData { discordPreference: DiscordPreference ->
            discordPreference.copy(
                discordToken = discordToken
            )
        }
    }

    fun setAccessUsedToken(token : String) {
        scope.launch {
            store.updateData { discordPreference: DiscordPreference ->
                discordPreference.copy(
                    accessTokenUsed = token
                )
            }
        }
    }

    fun getDiscordToken() : DiscordToken? = runBlocking {
        store.data.first().discordToken
    }

    fun getExchangeCode() : String? = runBlocking {
        store.data.first().exchangeCode
    }

    // Login State method
    suspend fun login(discordUser: DiscordUser) {
        store.updateData { discordPreference ->
            discordPreference.copy(
                discordUser = discordUser
            )
        }
    }

    // Logout State method
    fun logout() {
        scope.launch {
            store.updateData { discordPreference ->
                discordPreference.copy(
                    discordUser = null
                )
            }
        }
    }


    // Constants
    const val ENDPOINT = "https://discord.com/api/v9/"
    const val CDN_ENDPOINT = "https://cdn.discordapp.com"

    const val CLIENT_ID = "934278886412406814"
    const val CLIENT_SECRET = "e1xHywqylVMB6P4PjpL-5p_M0dmYHy79"
    const val REDIRECT_URI = "https://discord.com/channels/@me"

    const val LOGIN_PAGE = "oauth2/authorize?response_type=code&" +
            "client_id=${CLIENT_ID}&scope=identify%20guilds.join&" +
            "state=15773059ghq9183habn&" +
            "redirect_uri=${REDIRECT_URI}&prompt=consent"

    // Query Conditions
    const val IS_LOGGED = "channels/@me"
    const val REDIRECT_URI_REF = "redirect_uri="
}