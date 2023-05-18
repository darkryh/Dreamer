package com.ead.project.dreamer.presentation.player.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.data.utils.ui.DreamerUIMiniController
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment
import javax.inject.Inject

@AndroidEntryPoint
class MiniControllerFragment : MiniControllerFragment() {

    @Inject lateinit var castManager : CastManager
    private lateinit var dreamerUIMiniController: DreamerUIMiniController
    private lateinit var customButtonView : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        customButtonView = getButtonImageViewAt(2)
        dreamerUIMiniController = DreamerUIMiniController(customButtonView,castManager)
        uiMediaController?.bindViewToUIController(customButtonView, dreamerUIMiniController)
        return view
    }

}