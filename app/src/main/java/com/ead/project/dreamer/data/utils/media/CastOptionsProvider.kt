package com.ead.project.dreamer.data.utils.media

import android.content.Context
import android.content.Intent
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.ui.player.cast.ExpandedControlsActivity
import com.google.android.gms.cast.CredentialsData
import com.google.android.gms.cast.LaunchOptions
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.Session
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver
import com.google.android.gms.cast.framework.media.NotificationOptions

class CastOptionsProvider : OptionsProvider {

    companion object {
        const val CUSTOM_NAMESPACE = "urn:x-cast:custom_namespace"
    }

    override fun getCastOptions(context: Context): CastOptions {
        val supportedNamespaces: MutableList<String> = ArrayList()
        supportedNamespaces.add(CUSTOM_NAMESPACE)

        val notificationOptions = NotificationOptions.Builder()
            .setTargetActivityClassName(ExpandedControlsActivity::class.java.name)
            .build()

        val mediaOptions = CastMediaOptions.Builder()
            .setNotificationOptions(notificationOptions)
            .setExpandedControllerActivityClassName(ExpandedControlsActivity::class.java.name)
            //.setMediaIntentReceiverClassName(MyMediaIntentReceiver::class.java.name)
            .build()

        val credentialsData = CredentialsData.Builder()
            .setCredentials("{\"userId\": \"abc\"}")
            .build()

        val launchOptions = LaunchOptions.Builder()
            .setAndroidReceiverCompatible(false)
            .setCredentialsData(credentialsData)
            .build()

        return CastOptions.Builder()
            .setLaunchOptions(launchOptions)
            .setReceiverApplicationId(context.getString(R.string.app_id))
            .setCastMediaOptions(mediaOptions)
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }
}

internal class MyMediaIntentReceiver : MediaIntentReceiver() {
    override fun onReceiveActionTogglePlayback(currentSession: Session) {
        DreamerApp.showLongToast("onReceiveActionTogglePlayback")
    }

    override fun onReceiveActionMediaButton(currentSession: Session, intent: Intent) {
        DreamerApp.showLongToast("onReceiveActionMediaButton")
    }

    override fun onReceiveOtherAction(context: Context?, action: String, intent: Intent) {
        DreamerApp.showLongToast("onReceiveOtherAction")
    }
}