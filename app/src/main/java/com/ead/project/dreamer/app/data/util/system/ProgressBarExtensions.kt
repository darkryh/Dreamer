package com.ead.project.dreamer.app.data.util.system

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar

fun ProgressBar.centerGravity() {
    (layoutParams as LinearLayout.LayoutParams).gravity = Gravity.CENTER
}