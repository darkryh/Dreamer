package com.ead.project.dreamer.app.data.util.system

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


fun BottomSheetDialogFragment.setWidthMatchParent() {
    BottomSheetBehavior.from(requireView().parent as View).maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
}

fun BottomSheetDialogFragment.setStateExpanded() {
    BottomSheetBehavior.from(requireView().parent as View).state = BottomSheetBehavior.STATE_EXPANDED
}