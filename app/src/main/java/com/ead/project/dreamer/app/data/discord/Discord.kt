package com.ead.project.dreamer.app.data.discord

object Discord {

    // Constants
    const val ENDPOINT = "https://discord.com/api/v10/"
    const val CDN_ENDPOINT = "https://cdn.discordapp.com"

    const val CLIENT_ID = "934278886412406814"
    const val CLIENT_SECRET = "e1xHywqylVMB6P4PjpL-5p_M0dmYHy79"
    const val REDIRECT_URI = "https://discord.com/channels/@me"

    const val LOGIN_PAGE = "oauth2/authorize?response_type=code&" +
            "client_id=${CLIENT_ID}&" +
            "state=15773059ghq9183habn&" +
            "scope=identify%20email%20guilds.join&" +
            "redirect_uri=${REDIRECT_URI}&prompt=consent"

    // Query Conditions
    const val IS_LOGGED = "channels/@me"
    const val REDIRECT_URI_REF = "redirect_uri="

}