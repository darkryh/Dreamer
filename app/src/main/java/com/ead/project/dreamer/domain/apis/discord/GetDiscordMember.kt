package com.ead.project.dreamer.domain.apis.discord

import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.app.model.TypeAccount
import com.ead.project.dreamer.data.models.discord.DiscordToken
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.data.models.discord.GuildMember
import com.ead.project.dreamer.data.models.discord.SignInResult
import java.io.IOException
import javax.inject.Inject

class GetDiscordMember @Inject constructor(
    private val getDiscordUserToken: GetDiscordUserToken,
    private val getDiscordUserRefreshToken: GetDiscordUserRefreshToken,
    private val getDiscordUserData: GetDiscordUserData,
    private val getDiscordUserInGuild: GetDiscordUserInGuild,
    private val getDiscordUserInToGuild: GetDiscordUserInToGuild
) {

    @Throws(IOException::class)
    suspend operator fun invoke(code : String) : SignInResult {
        return try {
            val userToken = getDiscordUserToken(code).body()?:
            return errorResult(message = "Error reconocimiento de token de cambio.")

            val discordUser = getDiscordUserData(userToken.access_token).body()?:
            return errorResult(message = "Error sincronizando cuenta.")

            val guildMember = getDiscordUserInGuild(discordUser.id).body()

            return if (guildMember != null) {
                successfulResult(
                    discordUser = discordUser,
                    guildMember = guildMember
                )
            }
            else {
                val refreshToken : DiscordToken = getDiscordUserRefreshToken(userToken.refresh_token).body()?:
                return errorResult(message = "Error reconocimiento de refresh token.")

                successfulResult(
                    discordUser = discordUser,
                    guildMember = getDiscordUserInToGuild(refreshToken.access_token,discordUser.id).body()?:
                    return errorResult(message = "Error de ingreso en guild.")
                )
            }
        }
        catch (exception : IOException) {
            exception.printStackTrace()
            errorResult(message = "Error conexión : ${exception.message}")
        }
        catch (exception : Exception) {
            exception.printStackTrace()
            errorResult(message = exception.message)
        }
    }

    private fun successfulResult(discordUser: DiscordUser, guildMember: GuildMember) : SignInResult {
        return SignInResult(
            data = EadAccount(
                id = discordUser.id,
                typeAccount = TypeAccount.Discord,
                displayName = discordUser.username,
                email = discordUser.email,
                profileImage = discordUser.cdn_avatar,
                banner = discordUser.banner,
                locale = discordUser.locale,
                ranks = guildMember.roles
            ),
            errorMessage = null
        )
    }

    private fun errorResult(message : String?) : SignInResult {
        return SignInResult(
            data = null,
            errorMessage = message
        )
    }
}