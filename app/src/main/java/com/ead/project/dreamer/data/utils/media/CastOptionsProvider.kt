package com.ead.project.dreamer.data.utils.media

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import com.ead.project.dreamer.R
import com.ead.project.dreamer.presentation.player.cast.ExpandedControlsActivity
import com.google.android.gms.cast.CredentialsData
import com.google.android.gms.cast.LaunchOptions
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver
import com.google.android.gms.cast.framework.media.NotificationOptions

@Suppress("unused")
@SuppressLint("VisibleForTests")
class CastOptionsProvider : OptionsProvider {

    companion object {
        const val CUSTOM_NAMESPACE = "urn:x-cast:dreamer_space"
    }

    override fun getCastOptions(context: Context): CastOptions {
        val supportedNamespaces: MutableList<String> = ArrayList()
        supportedNamespaces.add(CUSTOM_NAMESPACE)

        val buttonActions: MutableList<String> = ArrayList()
        buttonActions.add(MediaIntentReceiver.ACTION_REWIND)
        buttonActions.add(MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK)
        buttonActions.add(MediaIntentReceiver.ACTION_FORWARD)
        buttonActions.add(MediaIntentReceiver.ACTION_STOP_CASTING)

        val compatButtonActionsIndices = intArrayOf(1, 3)

        val notificationOptions = NotificationOptions.Builder()
            .setActions(buttonActions, compatButtonActionsIndices)
            .setSkipStepMs(30 * DateUtils.SECOND_IN_MILLIS)
            .setTargetActivityClassName(ExpandedControlsActivity::class.java.name)
            .build()

        val mediaOptions = CastMediaOptions.Builder()
            .setNotificationOptions(notificationOptions)
            .setExpandedControllerActivityClassName(ExpandedControlsActivity::class.java.name)
            .setMediaSessionEnabled(true)
            .build()

        val credentialsData = CredentialsData.Builder()
            .setCredentials("{\"userId\": \"abc\"}")
            .build()

        val launchOptions = LaunchOptions.Builder()
            .setAndroidReceiverCompatible(true)
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