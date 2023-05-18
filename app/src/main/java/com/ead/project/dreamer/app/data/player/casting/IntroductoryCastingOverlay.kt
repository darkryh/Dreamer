package com.ead.project.dreamer.app.data.player.casting

import android.app.Activity
import androidx.mediarouter.app.MediaRouteButton
import com.ead.project.dreamer.R
import com.google.android.gms.cast.framework.IntroductoryOverlay

object IntroductoryCastingOverlay {

    var overlay : IntroductoryOverlay?= null

    fun get(activity : Activity,mediaRouteButton : MediaRouteButton,onDismissListener : () -> Unit) =
        IntroductoryOverlay.Builder(activity,mediaRouteButton)
            .setTitleText(activity.getString(R.string.casting_available))
            .setSingleTime()
            .setOverlayColor(R.color.orange_peel_dark)
            .setOnOverlayDismissedListener(onDismissListener)
            .build()

}