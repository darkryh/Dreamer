package com.ead.project.dreamer.data.utils.ui


import android.view.View
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.cast.framework.media.uicontroller.UIController

class DreamerUIController (private val mView: View) : UIController() {
    override fun onMediaStatusUpdated() {
        mView.visibility = View.VISIBLE
        mView.layoutParams.height = 120
        mView.layoutParams.width = 120
        (mView as ImageView).apply {
            load("https://i.ibb.co/6nfLSKL/logo-app.png") {
                transformations(CircleCropTransformation())
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}
