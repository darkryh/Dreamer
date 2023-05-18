package com.ead.project.dreamer.app.data.util.system

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout


fun ShimmerFrameLayout.show() {
    startShimmer()
    visibility = View.VISIBLE
}

fun ShimmerFrameLayout.hide() {
    stopShimmer()
    visibility = View.GONE
}