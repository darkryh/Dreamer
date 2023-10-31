package com.ead.project.dreamer.app

import android.os.Build
import com.ead.project.dreamer.BuildConfig

object AppInfo {

    const val isGoogleAppVersion = false

    const val name : String = "Dreamer"
    const val version : String = BuildConfig.VERSION_NAME
    val versionValue : Double = version.toDouble()
    const val versionCode : Int = BuildConfig.VERSION_CODE

    const val API_APP = "https://darkryh.github.io/Api_Dreamer/"
    const val WEB_APP = "https://dreamer-ead.net/"
    const val PLAY_STORE_APP = "https://play.google.com/store/apps/details?id=com.ead.project.dreamer"

    const val TOPIC = "Dreamer_Topic"

    const val CONTACT_DEVELOPER_EMAIL = "darkryhsthreatment@gmail.com"

    private val systemVersion = Build.VERSION.RELEASE
    private val model = Build.MODEL

    val userAgent = "Mozilla/5.0 (Linux; Android $systemVersion; $model) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/94.0.4606.61 Mobile Safari/537.36"
}