package com.ead.project.dreamer.data.utils.ui

import android.view.ScaleGestureDetector
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

class DreamerOnScaleGestureListener(
    private val player: StyledPlayerView
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    private var scaleFactor = 0f
    var isHorizontalMode : Boolean = false


    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor = detector.scaleFactor
        return true
    }
    override fun onScaleBegin(detector: ScaleGestureDetector) : Boolean =  true

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        if (isHorizontalMode)
            if (scaleFactor > 1) {
                when(DataStore.readInt(Constants.PREFERENCE_RESIZING_MODE, -1)) {
                    -1 -> player.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                    else -> player.resizeMode =
                        DataStore.readInt(Constants.PREFERENCE_RESIZING_MODE)
                }
            } else player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
}