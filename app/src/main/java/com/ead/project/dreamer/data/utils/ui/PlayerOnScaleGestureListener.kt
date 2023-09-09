package com.ead.project.dreamer.data.utils.ui

import android.view.ScaleGestureDetector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.domain.PreferenceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerOnScaleGestureListener(
    private val player: PlayerView,
    preferenceUseCase: PreferenceUseCase
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    private val preferences = preferenceUseCase.preferences
    private val scope : CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var scaleFactor = 0f
    var isHorizontalMode : Boolean = false

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor = detector.scaleFactor
        return true
    }
    override fun onScaleBegin(detector: ScaleGestureDetector) : Boolean =  true

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        scope.launch {
            if (isHorizontalMode)
                if (scaleFactor > 1) {
                    when(preferences.getInt(Constants.PREFERENCE_RESIZING_MODE, -1)) {
                        -1 -> player.resizeMode =
                            AspectRatioFrameLayout.RESIZE_MODE_FILL
                        else -> player.resizeMode =
                            preferences.getInt(Constants.PREFERENCE_RESIZING_MODE)
                    }
                } else {
                    player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
        }
    }
}