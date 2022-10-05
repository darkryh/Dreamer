package com.ead.project.dreamer.ui.player.cast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.ui.DreamerUIMiniController
import com.ead.project.dreamer.ui.player.cast.controller.MiniControllerFragment

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MiniControllerFragment : MiniControllerFragment() {

    private var castManager : CastManager = CastManager(true)
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