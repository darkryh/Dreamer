package com.ead.project.dreamer.app.data.util.system

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout


fun ViewGroup.gravityCenter() {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ).also { it.gravity = Gravity.CENTER }
}
