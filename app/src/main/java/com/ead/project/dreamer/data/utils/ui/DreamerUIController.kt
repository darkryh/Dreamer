package com.ead.project.dreamer.data.utils.ui


import android.view.View
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.google.android.gms.cast.framework.media.uicontroller.UIController

class DreamerUIController (private val view: View) : UIController() {
    override fun onMediaStatusUpdated() {
        view.visibility = View.VISIBLE
        view.layoutParams.height = 120
        view.layoutParams.width = 120
        view.background = DreamerLayout.getDrawable(R.drawable.background_circular)
        (view as ImageView).apply {
            load(R.mipmap.ic_launcher_round) {
                transformations(CircleCropTransformation())
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}
