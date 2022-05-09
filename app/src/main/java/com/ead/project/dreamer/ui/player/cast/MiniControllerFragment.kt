package com.ead.project.dreamer.ui.player.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ead.project.dreamer.data.utils.ui.DreamerUIController
import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MiniControllerFragment : MiniControllerFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val customButtonView = getButtonImageViewAt(1)
        val myCustomUiController = DreamerUIController(customButtonView)
        //uiMediaController?.bindViewToUIController(customButtonView, myCustomUiController)
        return view
    }
}