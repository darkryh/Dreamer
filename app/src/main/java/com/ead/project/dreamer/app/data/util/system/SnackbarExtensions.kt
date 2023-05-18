package com.ead.project.dreamer.app.data.util.system

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

fun Snackbar.getParentViewGroup() : ViewGroup {
    return view.findViewById<View>(com.google.android.material.R.id.snackbar_text)
        .parent as ViewGroup
}

fun Snackbar.getTextViewContent() : TextView {
    return view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
}