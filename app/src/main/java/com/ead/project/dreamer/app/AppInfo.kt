package com.ead.project.dreamer.app

import com.ead.project.dreamer.BuildConfig

object AppInfo {

    const val isGoogleAppVersion = false

    const val version : String = BuildConfig.VERSION_NAME
    val versionValue : Double = version.toDouble()
    const val versionCode : Int = BuildConfig.VERSION_CODE

    const val API_APP = "https://darkryh.github.io/Api_Dreamer/"
    const val WEB_APP = "https://dreamer-ead.net/"
    const val PLAY_STORE_APP = "https://play.google.com/store/apps/details?id=com.ead.project.dreamer"

    const val LOGO_URL = "https://i.ibb.co/6nfLSKL/logo-app.png"
    const val TOPIC = "Dreamer_Topic"

    const val CONTACT_DEVELOPER_EMAIL = "darkryhsthreatment@gmail.com"
}