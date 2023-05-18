package com.ead.project.dreamer.app.data.player.casting

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.mediarouter.app.MediaRouteButton
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import javax.inject.Inject

class CastBinding @Inject constructor(
    private val context: Context,
    private val castMediaSession: CastMediaSession
) {

    private val castingTextContent get() = context.getString(
        R.string.casting_in_device,
        castMediaSession.session.currentCastSession?.castDevice?.friendlyName
    )

    fun showIntroductoryOverlay(activity: Activity, mediaRouteButton: MediaRouteButton) {
        IntroductoryCastingOverlay.overlay?.remove()
        IntroductoryCastingOverlay.overlay = IntroductoryCastingOverlay.get(activity,mediaRouteButton) {
            IntroductoryCastingOverlay.overlay = null
        }
        IntroductoryCastingOverlay.overlay?.show()
    }

    fun showCastingTextView(text : TextView) {
        text.text = castingTextContent
        text.setVisibility(true)
    }

    fun hideCastingTextView(text: TextView) {
        text.setVisibility(false)
    }
}