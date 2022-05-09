package com.ead.project.dreamer.data.retrofit.model.discord


class Discord {

    companion object {

        //DATA

        const val ENDPOINT = "https://discord.com/api/v9/"

        const val CDN_ENDPOINT = "https://cdn.discordapp.com"

        const val SERVER_ID = "934335404172275732"

        const val CLIENT_ID = "934278886412406814"

        const val CLIENT_SECRET = "e1xHywqylVMB6P4PjpL-5p_M0dmYHy79"

        const val REDIRECT_URI = "https://discord.com/channels/@me"

        const val BASIC_RANK_ID = "946849009262280734"

        //const val CHANNEL_COMMUNITY_ID = "946935168206245898"

        //const val CHANNEL_SUPPORT_ID = "946935239991758849"

        const val LOGIN_PAGE = "oauth2/authorize?response_type=code&" +
                "client_id=$CLIENT_ID&scope=identify%20guilds.join&" +
                "state=15773059ghq9183habn&" +
                "redirect_uri=$REDIRECT_URI&prompt=consent"

        val PROFILE_IMAGE = "$CDN_ENDPOINT/avatars/${User.get()?.id}/${User.get()?.avatar}"

        const val USER_TOKEN = "USER_TOKEN"

        const val BOT_TOKEN = "OTM0Mjc4ODg2NDEyNDA2ODE0.YetxBA.VdGUP6VAzfWZDS8fVallYgka4aA"

        //VALUES TO WORK

        const val EXCHANGE_CODE = "EXCHANGE_CODE"

        const val ACCESS_TOKEN = "ACCESS_TOKEN"

        const val REFRESH_TOKEN = "REFRESH_TOKEN"

        const val USER_ME = "USER_ME"


        //QUERY CONDITIONS

        const val IS_LOGGED = "channels/@me"

        const val REDIRECT_URI_REF = "redirect_uri="
    }
}