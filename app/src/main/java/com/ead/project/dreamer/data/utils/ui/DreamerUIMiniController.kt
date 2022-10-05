package com.ead.project.dreamer.data.utils.ui


import android.view.View
import android.widget.ImageView
import coil.load
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.media.CastManager
import com.google.android.gms.cast.framework.media.uicontroller.UIController

class DreamerUIMiniController (view: View, private val castManager: CastManager?) : UIController() {

    private val imageView = view as ImageView

    override fun onMediaStatusUpdated() {
        imageView.visibility = View.VISIBLE
        imageView.layoutParams.height = 100
        imageView.layoutParams.width = 100
        imageView.load(R.drawable.ic_close_24)
        imageView.setOnClickListener { castManager?.stopCasting() }
    }

}
