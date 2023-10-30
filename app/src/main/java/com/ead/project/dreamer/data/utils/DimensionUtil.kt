package com.ead.project.dreamer.data.utils

import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.App
import kotlin.math.roundToInt

object DimensionUtil {

    private val context by lazy { App.Instance }
    private val resources = context.resources

    val portraitPlayPause = resources.getDimension(R.dimen.portrait_play_pause_button).roundToInt()
    val portraitControls = resources.getDimension(R.dimen.portrait_controls_button).roundToInt()

    val landscapePlayPause = resources.getDimension(R.dimen.landscape_play_pause_button).roundToInt()
    val landscapeControls = resources.getDimension(R.dimen.landscape_controls_button).roundToInt()

}